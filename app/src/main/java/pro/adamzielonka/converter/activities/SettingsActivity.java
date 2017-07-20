package pro.adamzielonka.converter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
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

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.activities.abstractes.PreferenceActivity;

import static pro.adamzielonka.converter.tools.Theme.getThemeID;
import static pro.adamzielonka.converter.tools.Theme.getThemeName;
import static pro.adamzielonka.converter.tools.Theme.getThemes;

public class SettingsActivity extends PreferenceActivity implements ListView.OnItemClickListener, GoogleApiClient.OnConnectionFailedListener {

    private View themeView;
    private View logInView;
    private View websiteView;
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onLoad() throws Exception {
        initAuth();
        super.onLoad();
        listView.setEmptyAdapter();
        listView.setOnItemClickListener(this);

        listView.addHeaderTitle(getString(R.string.pref_header_appearance));
        themeView = listView.addHeaderItem(getString(R.string.pref_title_theme));
        listView.addHeaderTitle(getString(R.string.pref_header_user));
        logInView = listView.addHeaderItem(getString(R.string.sign_in));
        listView.addHeaderTitle(getString(R.string.pref_header_about));
        listView.addHeaderItem(getString(R.string.pref_title_version), getString(R.string.app_version), false);
        websiteView = listView.addHeaderItem(getString(R.string.pref_title_website), getString(R.string.website));
    }

    public void onUpdate() {
        updateView(themeView, getThemeName(this));
        if (getUser() != null) {
            updateView(logInView, getString(R.string.sign_out), getUser().getEmail());
        } else {
            updateView(logInView, getString(R.string.sign_in), "");
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
        } else if (view.equals(logInView)) {
            if (getUser() != null) signOut();
            else signIn();
        } else if (view.equals(websiteView)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://adamzielonka.pro/"));
            startActivity(browserIntent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("theme")) {
            Intent settings = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(settings);
            overridePendingTransition(0, 0);
            finish();
        }
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
                    } else {
                        Toast.makeText(SettingsActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    onUpdate();
                    hideProgressDialog();
                });
    }
    //endregion

}
