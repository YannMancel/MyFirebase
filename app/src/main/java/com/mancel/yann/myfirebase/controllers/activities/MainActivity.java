package com.mancel.yann.myfirebase.controllers.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.mancel.yann.myfirebase.R;
import com.mancel.yann.myfirebase.models.api.UserRequests;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Yann MANCEL on 25/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.controllers.activities
 */
public class MainActivity extends BaseActivity {

    // FIELDS --------------------------------------------------------------------------------------

    @BindView(R.id.activity_main_coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.activity_main_button_login) Button mLogin;

    private static final int RC_SIGN_IN = 42;

    // METHODS -------------------------------------------------------------------------------------

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void configureDesign() {}

    @Override
    protected void onResume() {
        super.onResume();

        // Updates the Login Button
        this.updateLoginButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handles the response after that the Sign In Activity is finished
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    @OnClick(R.id.activity_main_button_login)
    public void onClickToLogin(View view) {
        // Tests if the user is logged
        if (this.isCurrentUserLogged()) {
            // Starts the ProfileActivity
            this.startProfileActivity();
        }
        else {
            // Starts the Sign In Activity that are automatically designed by the Firebase UI
            this.startSignInActivity();
        }
    }

    @OnClick(R.id.activity_main_button_chat)
    public void onClickToChat(View view) {
        // Tests if the user is logged
        if (this.isCurrentUserLogged()) {
            // Starts the ChatActivity
            this.startChatActivity();
        }
        else {
            // Shows SnackBar object with a message
            this.showSnackBar(this.mCoordinatorLayout, getString(R.string.user_not_logged));
        }
    }

    /**
     * Starts the Sign In Activity that are automatically designed by the Firebase UI
     */
    private void startSignInActivity() {
        // Chooses authentication providers (Email, Google and Facebook)
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                                         new AuthUI.IdpConfig.GoogleBuilder().build(),
                                                         new AuthUI.IdpConfig.FacebookBuilder().build());

        // Creates and launches sign-in intent
        startActivityForResult(AuthUI.getInstance()
                                     .createSignInIntentBuilder()
                                     .setTheme(R.style.LoginTheme)
                                     .setAvailableProviders(providers)
                                     .setIsSmartLockEnabled(false, true)
                                     .setLogo(R.drawable.ic_logo_auth)
                                     .build(),
                               RC_SIGN_IN);
    }

    /**
     * Starts the ProfileActivity
     */
    private void startProfileActivity() {
        // Creates an Intent object
        Intent intent = new Intent(this, ProfileActivity.class);

        // Creates and launches the Intent object
        startActivity(intent);
    }

    /**
     * Starts the ChatActivity
     */
    private void startChatActivity() {
        // Creates an Intent object
        Intent intent = new Intent(this, ChatActivity.class);

        // Creates and launches the Intent object
        startActivity(intent);
    }

    /**
     * Handles the response after that the Sign In Activity is finished
     * @param requestCode a integer that contains the value corresponding to the startActivityForResult method
     * @param resultCode a integer that contains the value corresponding to result code when the activity finishes
     * @param data an Intent object that contains the saved data
     */
    private void handleResponseAfterSignIn(final int requestCode, final int resultCode, @Nullable final Intent data) {
        // Retrieves the state of the Sign In Activity to the login
        if (requestCode == RC_SIGN_IN) {
            // Creates an IdpResponse object that contains the user choice
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Creates an user in Firestore
                this.createUserInFirestore();

                // Shows SnackBar object with a message
                this.showSnackBar(this.mCoordinatorLayout, getString(R.string.success_to_create_account));
            }
            else {
                // Displays a short message when the sign in is not validated
                this.noSignInResponse(response);
            }
        }
    }

    /**
     * Creates an user in Firestore
     */
    private void createUserInFirestore() {
        // Check if the user is logged
        if (this.isCurrentUserLogged()) {
            // UId
            final String uid = this.getCurrentUser().getUid();

            // Username
            final String username = this.getCurrentUser().getDisplayName();

            // Url picture
            final String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() :
                                                                                      null;

            UserRequests.createUser(uid, username, urlPicture)
                        .addOnFailureListener(this.onFailureListener());
        }
    }

    /**
     * Displays a short message when the sign in is not validated
     * @param response an IdpResponse object that contains the choice of the user
     */
    private void noSignInResponse(final IdpResponse response) {
        // Sign in failed. If response is null the user canceled the
        // sign-in flow using the back button. Otherwise check
        // response.getError().getErrorCode() and handle the error.
        if (response == null) {
            // Shows SnackBar object with a message
            this.showSnackBar(this.mCoordinatorLayout, getString(R.string.error_to_create_account_cancel));
        }
        else {
            // According the error code
            switch (response.getError().getErrorCode()) {
                case ErrorCodes.NO_NETWORK : {
                    // Shows SnackBar object with a message
                    this.showSnackBar(this.mCoordinatorLayout, getString(R.string.error_to_create_account_no_network));
                    break;
                }
                case ErrorCodes.UNKNOWN_ERROR : {
                    // Shows SnackBar object with a message
                    this.showSnackBar(this.mCoordinatorLayout, getString(R.string.error_to_create_account_unknown_error));
                    break;
                }
                default: {}
            }
        }
    }

    /**
     * Shows SnackBar object with a message
     * @param coordinatorLayout a CoordinatorLayout object that contains the view
     * @param message a String object that contains the message to display
     */
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        // Creates a Snackbar object
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Updates the Login Button
     */
    private void updateLoginButton() {
        this.mLogin.setText(this.isCurrentUserLogged() ? getString(R.string.button_login_text_logged) :
                                                         getString(R.string.button_login_text_not_logged));
    }
}
