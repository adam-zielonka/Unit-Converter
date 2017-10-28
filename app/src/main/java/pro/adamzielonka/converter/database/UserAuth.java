package pro.adamzielonka.converter.database;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pro.adamzielonka.converter.R;
import pro.adamzielonka.converter.components.MyProgressDialog;
import pro.adamzielonka.converter.models.database.User;
import pro.adamzielonka.items.dialog.EditDialogBuilder;

import static pro.adamzielonka.converter.database.FireBaseNames.USERS;
import static pro.adamzielonka.converter.database.FireBaseNames.USERS_NAME;
import static pro.adamzielonka.converter.database.FireBaseNames.USERS_NAMES;
import static pro.adamzielonka.converter.tools.Message.showError;
import static pro.adamzielonka.converter.tools.Preferences.getPreferences;
import static pro.adamzielonka.converter.tools.Preferences.setPreferences;

public class UserAuth {
    public static final int RC_SIGN_IN = 9001;
    private FragmentActivity activity;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private MyProgressDialog myProgressDialog;

    private OnAuthResult onAuthResult;

    public UserAuth(FragmentActivity activity, OnAuthResult onAuthResult) {
        this.activity = activity;
        this.onAuthResult = onAuthResult;
        myProgressDialog = new MyProgressDialog(activity);

        mFirestore = FirebaseFirestore.getInstance();
        mGoogleApiClient = newGoogleApiClient();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public interface OnAuthResult {
        void onAuthResult();
    }

    //region init auth
    private GoogleSignInOptions newGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }

    private void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(activity, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private GoogleApiClient newGoogleApiClient() {
        return new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, this::onConnectionFailed)
                .addApi(com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API, newGoogleSignInOptions())
                .build();
    }
    //endregion

    public void signIn() {
        activity.startActivityForResult(com.google.android.gms.auth.api.Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
    }

    public void signOut() {
        setUserName("");
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> onAuthResult.onAuthResult());
    }

    public void getSignInResultFromIntent(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) firebaseAuthWithGoogle(result.getSignInAccount());
        else onAuthResult.onAuthResult();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        myProgressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        onAuthSuccess();
                    } else {
                        Toast.makeText(activity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        myProgressDialog.hide();
                    }
                });
    }

    private void onAuthSuccess() {
        String userId = getUid();
        if (userId == null) return;

        mFirestore.collection(USERS).document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Object name = documentSnapshot.getData().get(USERS_NAME);
                setUserName(name.toString());
                onAuthResult.onAuthResult();
                myProgressDialog.hide();
            } else
                createUser(false, "", "");
        }).addOnFailureListener(e -> {
            onAuthResult.onAuthResult();
            myProgressDialog.hide();
            signOut();
        });
    }

    public void changeUserName() {
        createUser(true, getUserName(), "");
    }

    private void createUser(boolean changeName, String name, String error) {
        myProgressDialog.show();
        new EditDialogBuilder(activity)
                .setError(error)
                .setValue(name)
                .setAction(newUserName -> writeNewUser(changeName, getUid(), (String) newUserName))
                .setNegativeAction((d, i) -> {
                    if (!changeName) signOut();
                    onAuthResult.onAuthResult();
                    myProgressDialog.hide();
                })
                .setTitle(R.string.dialog_set_user_name)
                .create()
                .setCancelable(changeName)
                .show();
    }

    private void writeNewUser(boolean changeName, String userId, String name) {
        User user = new User(name, userId);
        mFirestore.collection(USERS).whereEqualTo(USERS_NAME, name).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.d(USERS, document.getId() + " => " + document.getData());
                        }
                        if (task.getResult().isEmpty())
                            finishCreateUser(user);
                        else
                            createUser(changeName, name, activity.getString(R.string.error_name_already_exist));
                    } else {
                        showError(activity, R.string.error_create_user);
                        Log.d(USERS, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void finishCreateUser(User user) {
        mFirestore.collection(USERS).document(user.userId).set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setUserName(user.name);
                    } else {
                        showError(activity, R.string.error_create_user);
                        signOut();
                    }
                    onAuthResult.onAuthResult();
                    myProgressDialog.hide();
                });
    }

    private void setUserName(String name) {
        setPreferences(activity, USERS_NAMES, name);
    }

    public String getUserName() {
        return getPreferences(activity).getString(USERS_NAMES, "");
    }

    //region user
    public static String getUid() {
        return getUser() != null ? getUser().getUid() : null;
    }

    public static FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    //endregion
}
