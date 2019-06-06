package com.mancel.yann.myfirebase.controllers.activities;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mancel.yann.myfirebase.R;
import com.mancel.yann.myfirebase.models.api.UserRequests;
import com.mancel.yann.myfirebase.models.pojos.User;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Yann MANCEL on 27/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.controllers.activities
 */
public class ProfileActivity extends BaseActivity {

    // FIELDS --------------------------------------------------------------------------------------

    @BindView(R.id.activity_profile_image_view_profile) ImageView mImageProfile;
    @BindView(R.id.activity_profile_edit_text_username) TextInputEditText mUsername;
    @BindView(R.id.activity_profile_text_view_email) TextView mEmail;
    @BindView(R.id.activity_profile_progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.activity_profile_check_box_is_mentor) CheckBox mCheckBox;

    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int UPDATE_USERNAME_DATABASE = 30;

    // METHODS -------------------------------------------------------------------------------------

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_profile;
    }

    @Override
    protected void configureDesign() {
        // Configures the ToolBar field
        this.configureToolBar();

        // Updates the user data
        this.updateUserData();
    }

    // CheckBox: IsMentor
    @OnClick(R.id.activity_profile_check_box_is_mentor)
    public void onClickCheckBoxIsMentor() {
        this.updateUserIsMentorInFirestoreOfFirebase();
    }

    // Button: Update
    @OnClick(R.id.activity_profile_button_update)
    public void onClickButtonToUpdate() {
        this.updateUsernameInFirestoreOfFirebase();
    }

    // Button: Sign Out
    @OnClick(R.id.activity_profile_button_sign_out)
    public void onClickButtonToSignOut() {
        this.signOutUserAccountInAuthenticationOfFirebase();
    }

    // Button: Delete
    @OnClick(R.id.activity_profile_button_delete)
    public void onClickButtonToDelete() {
        // Creates Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Customization
        builder.setTitle(getString(R.string.delete_account))
               .setMessage(getString(R.string.popup_message_confirmation_delete_account))
               .setPositiveButton(getString(R.string.ok_text),
                                  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Deletes the account and user data of Firebase
                                        deleteAccountAndUserDataOfFirebase();
                                    }
                                  })
               .setNegativeButton(getString(R.string.no_text), null)
               .create()
               .show();
    }

    /**
     * Update the "isMentor" data on the "users" collection in Firestore of Firebase
     */
    private void updateUserIsMentorInFirestoreOfFirebase() {
        // Checks if the user is logged
        if (isCurrentUserLogged()) {
            UserRequests.updateIsMentor(this.getCurrentUser().getUid(), this.mCheckBox.isChecked())
                        .addOnFailureListener(this.onFailureListener());
        }
    }

    /**
     * Update the "isMentor" data on the "users" collection in Firestore of Firebase
     */
    private void updateUsernameInFirestoreOfFirebase() {
        // Checks if the user is logged
        if (isCurrentUserLogged()) {
            // The ProgressBar is now visible
            this.mProgressBar.setVisibility(View.VISIBLE);

            // Retrieves the new username
            final String username = this.mUsername.getText().toString();

            if (!username.isEmpty() && !username.equals(getString(R.string.info_no_username_found))) {
                UserRequests.updateUsername(this.getCurrentUser().getUid(), username)
                            .addOnFailureListener(this.onFailureListener())
                            .addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(UPDATE_USERNAME_DATABASE));
            }
        }
    }

    /**
     * Signs out the user account in the authentication of Firebase
     */
    private void signOutUserAccountInAuthenticationOfFirebase() {
        // Checks if the user is logged
        if (isCurrentUserLogged()) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(this.getOnCompleteListener(SIGN_OUT_TASK));
        }
    }

    /**
     * Deletes the account and user data of Firebase
     */
    private void deleteAccountAndUserDataOfFirebase() {
        // Checks if the user is logged
        if (isCurrentUserLogged()) {
            // Deletes the account of the authentication of Firebase
            AuthUI.getInstance()
                    .delete(this)
                    .addOnCompleteListener(this.getOnCompleteListener(DELETE_USER_TASK));

            // Deletes the user data of Firestore of Firebase
            UserRequests.deleteUser(this.getCurrentUser().getUid())
                        .addOnFailureListener(this.onFailureListener());
        }
    }

    /**
     * Returns the OnSuccessListener<Void> to retrieves the user action on Firebase
     * @param origin a integer that contains the request type
     * @return an OnSuccessListener<Void> object that is a listener
     */
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    // 8 - Hiding Progress bar after request completed
                    case UPDATE_USERNAME_DATABASE:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;
                    //...
                }
            }
        };
    }

    /**
     * Returns the OnCompleteListener to retrieves the user action on Firebase
     * @param origin a integer that contains the used button type
     * @return an OnCompleteListener object that is a listener
     */
    private OnCompleteListener<Void> getOnCompleteListener (final int origin) {
        return new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // According to the used button
                switch (origin){
                    case SIGN_OUT_TASK : {
                        finish();
                        break;
                    }
                    case DELETE_USER_TASK : {
                        finish();
                        break;
                    }
                    default: {}
                }
            }
        };
    }

    /**
     * Updates the user data
     */
    private void updateUserData() {
        // Updates the UI
        if (this.isCurrentUserLogged()) {
            // Update ImageView (mImageProfile): If the user have a profile image
            if (this.getCurrentUser().getPhotoUrl() != null) {
                // Loads the profile image into the ImageView field
                Glide.with(this)
                     .load(this.getCurrentUser().getPhotoUrl())
                     .apply(RequestOptions.circleCropTransform())
                     .into(this.mImageProfile);
            }

            // Update TextView (mEmail):
            final String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) :
                    this.getCurrentUser().getEmail();

            this.mEmail.setText(email);

            // Gets additional data from Firestore (isMentor & Username)
            UserRequests.getUser(this.getCurrentUser().getUid())
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    // Deserializes from Firestore to Java object
                                                    User currentUser = documentSnapshot.toObject(User.class);

                                                    // Update TextInputEditText (mUsername):
                                                    final String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) :
                                                                                                                           currentUser.getUsername();

                                                    mUsername.setText(username);

                                                    // Update CheckBox (mCheckBox)
                                                    mCheckBox.setChecked(currentUser.getIsMentor());
                                                }
                                            });

            /*
            // Update TextInputEditText (mUsername):
            final String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) :
                                                                                                this.getCurrentUser().getDisplayName();

            this.mUsername.setText(username);
            */
        }
    }
}
