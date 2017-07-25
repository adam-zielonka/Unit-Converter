package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import static pro.adamzielonka.converter.tools.Language.getLanguageFromID;
import static pro.adamzielonka.converter.tools.Language.getLanguageID;
import static pro.adamzielonka.converter.tools.Language.getLanguages;
import static pro.adamzielonka.converter.tools.Language.setLanguage;
import static pro.adamzielonka.converter.tools.Theme.getThemeID;
import static pro.adamzielonka.converter.tools.Theme.getThemeName;
import static pro.adamzielonka.converter.tools.Theme.getThemes;

public class SettingsActivity extends PreferenceActivity implements ListView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener {

    private View themeView;
    private View langView;
    private View logInView;
    private View userNameView;
    private View websiteView;
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onLoad() throws Exception {
        setTitle(R.string.title_activity_settings);
        initAuth();
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.pref_header_appearance));
        themeView = listView.addHeaderItem(getString(R.string.pref_title_theme));
        langView = listView.addHeaderItem(getString(R.string.pref_title_language));
        listView.addHeaderTitle(getString(R.string.pref_header_user));
        logInView = listView.addHeaderItem(getString(R.string.pref_title_sign_in));
        userNameView = listView.addHeaderItem(getString(R.string.pref_title_user_name));
        listView.addHeaderTitle(getString(R.string.pref_header_about));
        disableView(listView.addHeaderItem(getString(R.string.pref_title_version), getString(R.string.app_version)));
        websiteView = listView.addHeaderItem(getString(R.string.pref_title_website), getString(R.string.website));
    }

    public void onUpdate() {
        updateView(themeView, getThemeName(this));
        updateView(langView, getResources().getConfiguration().locale.getLanguage());

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
        if (view.equals(themeView)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.pref_title_theme)
                    .setSingleChoiceItems(getThemes(this), getThemeID(this), (dialogInterface, i) -> {
                        int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("theme", selectedPosition + "");
                        editor.apply();
                        dialogInterface.dismiss();
                        onUpdate();
                    })
                    .setCancelable(true)
                    .show();
        } else if (view.equals(langView)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.pref_title_language)
                    .setSingleChoiceItems(getLanguages(this), getLanguageID(this), (dialogInterface, i) -> {
                        int selectedPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                        setLanguage(this, getLanguageFromID(this, selectedPosition));
                        dialogInterface.dismiss();
                        restart();
                    })
                    .setCancelable(true)
                    .show();
        } else if (view.equals(logInView)) {
            if (getUser() != null) signOut();
            else signIn();
        } else if (view.equals(websiteView)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://adamzielonka.pro/"));
            startActivity(browserIntent);
        } else if (view.equals(userNameView)) {
            if (getUser() != null) {
                showProgressDialog();
                createUser(true, getUserName(), "");
            }
        }
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        setUserName("");
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> onUpdate());
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
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                onUpdate();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mFirebaseAuth.getCurrentUser();
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
        EditText editText = getDialogEditText(name, error);
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_set_user_name)
                .setCancelable(false)
                .setView(editText.getRootView())
                .setPositiveButton(R.string.dialog_create, (dialogInterface, i) -> {
                    String newUnitName = editText.getText().toString();
                    writeNewUser(changeName, getUid(), newUnitName);
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    if (!changeName) signOut();
                    onUpdate();
                    hideProgressDialog();
                }).show();
    }

    private void createUserName(String name) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> map = new HashMap<>();
        map.put(name, getUid());
        mDatabase.child("usernames").updateChildren(map);
    }

    private void writeNewUser(boolean changeName, String userId, String name) {
        User user = new User(name);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    createUserName(name);
                    setUserName(name);
                    onUpdate();
                    hideProgressDialog();
                })
                .addOnFailureListener(e -> createUser(changeName, name, getString(R.string.error_name_already_exist)));
    }

    private void setUserName(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", name);
        editor.apply();
    }

    private String getUserName() {
        return preferences.getString("username", "");
    }
    //endregion

}
