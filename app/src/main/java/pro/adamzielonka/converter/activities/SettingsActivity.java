package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.PreferenceActivity;
import pro.adamzielonka.converter.models.database.User;
import pro.adamzielonka.itemsview.classes.Item;

import static pro.adamzielonka.converter.tools.Language.getLanguageFromID;
import static pro.adamzielonka.converter.tools.Language.getLanguageID;
import static pro.adamzielonka.converter.tools.Language.getLanguages;
import static pro.adamzielonka.converter.tools.Language.setLanguage;

public class SettingsActivity extends PreferenceActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_settings);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initAuth();

        new Item.Builder(this)
                .setTitleHeader(R.string.pref_header_appearance)
                .setTitle(R.string.pref_title_theme)
                .setUpdate(() -> theme.getName())
                .setArray(() -> theme.getArray())
                .setPosition(() -> theme.getID())
                .setAction(id -> theme.setID((Integer) id))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_language)
                .setUpdate(() -> getResources().getConfiguration().locale.getLanguage())
                .setArray(() -> getLanguages(this))
                .setPosition(() -> getLanguageID(this))
                .setAction(position -> {
                    setLanguage(this, getLanguageFromID(this, (Integer) position));
                    restart();
                }).add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.pref_header_user)
                .setTitle(() -> getUser() != null ? R.string.pref_title_sign_out : R.string.pref_title_sign_in)
                .setUpdate(() -> getUser() != null ? getUser().getEmail() : "")
                .setAction(() -> getUser() != null ? signOut() : signIn())
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(getString(R.string.pref_title_user_name))
                .setIf(() -> getUser() != null)
                .setUpdate(this::getUserName)
                .setElseUpdate(() -> "")
                .setAction(() -> {
                    showProgressDialog();
                    createUser(true, getUserName(), "");
                }).add(itemsView);

        new Item.Builder(this)
                .setTitleHeader(R.string.pref_header_about)
                .setTitle(R.string.pref_title_version)
                .setUpdate(() -> getString(R.string.app_version))
                .add(itemsView);
        new Item.Builder(this)
                .setTitle(R.string.pref_title_website)
                .setUpdate(() -> getString(R.string.website))
                .setAction(() -> startWebsite("https://adamzielonka.pro/"))
                .add(itemsView);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("theme")) {
            restart();
        }
    }

    protected void restart() {
        Intent settings = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(settings);
        overridePendingTransition(0, 0);
        finish();
    }

    //region sign in out
    private void initAuth() {
        mGoogleApiClient = newGoogleApiClient();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private GoogleSignInOptions newGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    private GoogleApiClient newGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, newGoogleSignInOptions())
                .build();
    }

    private boolean signIn() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
        return true;
    }

    private boolean signOut() {
        setUserName("");
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> onUpdate());
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) firebaseAuthWithGoogle(result.getSignInAccount());
            else onUpdate();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onAuthSuccess();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onAuthSuccess() {
        String userId = getUid();
        if (userId == null) return;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            createUser(false, "", "");
                        } else {
                            setUserName(user.username);
                            onUpdate();
                            hideProgressDialog();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        onUpdate();
                        hideProgressDialog();
                    }
                });
    }

    private void createUser(boolean changeName, String name, String error) {
        new Item.Builder(this)
                .setTitle(R.string.dialog_set_user_name)
                .setError(error)
                .setUpdate(() -> name)
                .setAction(newUserName -> writeNewUser(changeName, getUid(), (String) newUserName))
                .setCancelAction(() -> {
                    if (!changeName) signOut();
                    onUpdate();
                    hideProgressDialog();
                }).show();
    }

    private void createUserName(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put(name, getUid());
        mDatabase.child("usernames").updateChildren(map);
    }

    private void writeNewUser(boolean changeName, String userId, String name) {
        User user = new User(name);
        mDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> finishCreateUser(name))
                .addOnFailureListener(e -> createUser(changeName, name, getString(R.string.error_name_already_exist)));
    }

    private void finishCreateUser(String name) {
        createUserName(name);
        setUserName(name);
        onUpdate();
        hideProgressDialog();
    }

    private void setUserName(String name) {
        setPreferences("username", name);
    }

    private String getUserName() {
        return preferences.getString("username", "");
    }

    @Override
    public void onUpdate() {
        itemsView.onUpdate();
    }
    //endregion

}
