package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import pro.adamzielonka.converter.tools.Item;

import static pro.adamzielonka.converter.tools.Language.getLanguageFromID;
import static pro.adamzielonka.converter.tools.Language.getLanguageID;
import static pro.adamzielonka.converter.tools.Language.getLanguages;
import static pro.adamzielonka.converter.tools.Language.setLanguage;

public class SettingsActivity extends PreferenceActivity
        implements ListView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener {

    private View logInView;
    private View userNameView;
    private static final int RC_SIGN_IN = 9001;

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void addItems() {
        setTitle(R.string.title_activity_settings);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initAuth();

        Item.Builder(R.string.pref_header_appearance).add(this);
        addItemList(R.string.pref_title_theme, () -> theme.getName(), () -> theme.getArray(), () -> theme.getID(), position -> theme.setID(position));
        addItemList(R.string.pref_title_language, () -> getResources().getConfiguration().locale.getLanguage(),
                () -> getLanguages(this), () -> getLanguageID(this), position -> {
                    setLanguage(this, getLanguageFromID(this, position));
                    restart();
                });
        addItemTitle(R.string.pref_header_user);
        Item.Builder(R.string.pref_title_sign_in)
                .condition(() -> getUser() != null)
                .update(() -> getUser().getEmail())
                .elseUpdate(() -> "")
                .alert(() -> {
                    if (getUser() != null) signOut();
                    else signIn();
                })
                .add(this);
        logInView = listView.addItem(getString(R.string.pref_title_sign_in));
        userNameView = listView.addItem(getString(R.string.pref_title_user_name));
        addItemTitle(R.string.pref_header_about);
        addItemText(R.string.pref_title_version, () -> getString(R.string.app_version));
        addItemText(R.string.pref_title_website, () -> getString(R.string.website), () ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://adamzielonka.pro/"))));
    }

    @Override
    public void onUpdate() throws Exception {
        super.onUpdate();

        if (getUser() != null) {
            updateView(logInView, getString(R.string.pref_title_sign_out), getUser().getEmail());
            updateView(userNameView, getString(R.string.pref_title_user_name), getUserName());
        } else {
            updateView(logInView, getString(R.string.pref_title_sign_in), "");
            hideView(userNameView);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (view.equals(logInView)) {
            if (getUser() != null) signOut();
            else signIn();

        } else if (view.equals(userNameView)) {
            if (getUser() != null) {
                showProgressDialog();
                createUser(true, getUserName(), "");
            }
        } else super.onItemClick(adapterView, view, position, l);
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

    private void signIn() {
        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
    }

    private void signOut() {
        setUserName("");
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> onSave());
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
            else onSave();
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
                            onSave();
                            hideProgressDialog();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        onSave();
                        hideProgressDialog();
                    }
                });
    }

    private void createUser(boolean changeName, String name, String error) {
        EditText editText = getDialogEditText(name, error);
        getAlertDialog(R.string.dialog_set_user_name)
                .setView(editText.getRootView())
                .setPositiveButton(R.string.dialog_create, (dialogInterface, i) -> {
                    String newUnitName = editText.getText().toString();
                    writeNewUser(changeName, getUid(), newUnitName);
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    if (!changeName) signOut();
                    onSave();
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
        onSave();
        hideProgressDialog();
    }

    private void setUserName(String name) {
        setPreferences("username", name);
    }

    private String getUserName() {
        return preferences.getString("username", "");
    }
    //endregion

}
