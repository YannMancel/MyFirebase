package com.mancel.yann.myfirebase.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancel.yann.myfirebase.R;
import com.mancel.yann.myfirebase.models.api.MessageRequests;
import com.mancel.yann.myfirebase.models.api.UserRequests;
import com.mancel.yann.myfirebase.models.pojos.Message;
import com.mancel.yann.myfirebase.models.pojos.User;
import com.mancel.yann.myfirebase.views.ChatAdapter;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends BaseActivity implements ChatAdapter.MessageListerner{

    // FIELDS --------------------------------------------------------------------------------------

    @BindView(R.id.activity_chat_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.activity_chat_text_view_recycler_view_empty) TextView mTextViewRecyclerViewEmpty;
    @BindView(R.id.activity_chat_message_edit_text) TextInputEditText mEditTextMessage;
    @BindView(R.id.activity_chat_image_chosen_preview) ImageView mImageViewPreview;

    private ChatAdapter mChatAdapter;
    @Nullable
    private User mCurrentUser;
    private String mCurrentChatName;
    private Uri mUriImageSelected;

    private static final String CHAT_NAME_ANDROID = "android";
    private static final String CHAT_NAME_BUG = "bug";
    private static final String CHAT_NAME_FIREBASE = "firebase";

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;

    private static final int RC_CHOSEN_PHOTO = 200;

    // METHODS -------------------------------------------------------------------------------------

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_chat;
    }

    @Override
    protected void configureDesign() {
        // Configures the ToolBar field
        this.configureToolBar();

        // Retrieves the current user from Firestore of Firebase
        this.getCurrentUserFromFirestore();

        // Configures the RecyclerView field
        this.configureRecyclerView(CHAT_NAME_ANDROID);
    }

    @Override
    public void onDataChanged() {
        // Shows TextView in case RecyclerView is empty
        this.mTextViewRecyclerViewEmpty.setVisibility(this.mChatAdapter.getItemCount() == 0 ? View.VISIBLE :
                                                                                              View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Uses EasyPermissions library [For android versions >= API 23]
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handles the response
        this.handleResponse(requestCode, resultCode, data);
    }

    @OnClick(R.id.activity_chat_send_button)
    public void onClickSendMessage() {
        this.sendMessageToFirestore();
    }

    @OnClick({ R.id.activity_chat_android_button, R.id.activity_chat_firebase_button, R.id.activity_chat_bug_button})
    public void onClickChatButtons(View view) {
        // Retrieves the tag corresponding to the View object in parameter
        final int buttonTag =  Integer.parseInt(view.getTag().toString());

        // Re-Configures the RecyclerView depending on chosen chat
        switch (buttonTag){
            case 10:
                this.configureRecyclerView(CHAT_NAME_ANDROID);
                break;
            case 20:
                this.configureRecyclerView(CHAT_NAME_FIREBASE);
                break;
            case 30:
                this.configureRecyclerView(CHAT_NAME_BUG);
                break;
        }
    }

    @OnClick(R.id.activity_chat_add_file_button)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile() {
        this.chooseImageFromPhone();
    }

    /**
     * Sends a message to Firestore of Firebase
     */
    private void sendMessageToFirestore() {
        // Checks if the EditText is not empty and the current user exists
        if (!TextUtils.isEmpty(this.mEditTextMessage.getText()) && this.mCurrentUser != null) {
            // Checks if the user sends an image
            if (this.mImageViewPreview.getDrawable() == null) {
                // Creates a new message to Firestore
                MessageRequests.createMessageForChat(this.mEditTextMessage.getText().toString(), this.mCurrentUser, this.mCurrentChatName)
                        .addOnFailureListener(this.onFailureListener());
            }
            else {
                // SEND A IMAGE + TEXT IMAGE
                this.uploadPhotoInFirebaseAndSendMessage(this.mEditTextMessage.getText().toString());

                // Clears the ImageView of the image
                this.mImageViewPreview.setImageDrawable(null);
            }

            // Clears the EditText
            this.mEditTextMessage.setText("");
        }
    }

    /**
     * Uploads the photo in storage of Firebase and send the message
     */
    private void uploadPhotoInFirebaseAndSendMessage(final String message) {
        // Generates an unique string
        String uuid = UUID.randomUUID().toString();

        // Uploads the photo in the storage of Firebase
        StorageReference mImageRef = FirebaseStorage.getInstance()
                                                    .getReference(uuid);

        mImageRef.putFile(this.mUriImageSelected)
                 .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Retrieves the URL of the picture in the storage of Firebase
                        String pathImageSavedInFirebase = taskSnapshot.getStorage().getDownloadUrl().toString();
                        // taskSnapshot.getMetadata().getDownloadUrl() is deprecated

                        // Creates a message on Firestore of Firebase
                        MessageRequests.createMessageWithImageForChat(message, mCurrentUser, pathImageSavedInFirebase, mCurrentChatName)
                                       .addOnFailureListener(onFailureListener());
                    }
                 })
                .addOnFailureListener(this.onFailureListener());
    }

    /**
     * Chooses an image from phone
      */
    private void chooseImageFromPhone() {
        // Checks if the permission is accepted
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_acess), RC_IMAGE_PERMS, PERMS);
            return;
        }

        // Creates an Intent to select an image from phone
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Starts the activity via the Intent.ACTION_PICK
        startActivityForResult(intent, RC_CHOSEN_PHOTO);
    }

    /**
     * Handles the response
     * @param requestCode a integer that contains the value corresponding to the startActivityForResult method
     * @param resultCode a integer that contains the value corresponding to result code when the activity finishes
     * @param data an Intent object that contains the saved data
     */
    private void handleResponse(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_CHOSEN_PHOTO) {
            if (resultCode == RESULT_OK) {
                // Retrieves the Uri of the image
                this.mUriImageSelected = data.getData();

                // Loads the image into the ImageView field
                Glide.with(this)
                        .load(this.mUriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.mImageViewPreview);
            }
            else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Retrieves the current user from Firestore of Firebase
     */
    private void getCurrentUserFromFirestore() {
        UserRequests.getUser(this.getCurrentUser().getUid())
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                               // Deserializes from Firestore to Java object
                                               mCurrentUser = documentSnapshot.toObject(User.class);
                                            }
                                          });
    }

    /**
     * Configures RecyclerView with a Query
     * @param chatName aString object that contains the collection name from Firestore of Firebase
     */
    private void configureRecyclerView(String chatName) {
        // Tracks current chat name
        this.mCurrentChatName = chatName;

        // Configures Adapter
        this.mChatAdapter = new ChatAdapter(this.generateOptionsForAdapter(MessageRequests.getAllMessageForChat(this.mCurrentChatName)),
                                            Glide.with(this),
                                            this,
                                            this.getCurrentUser().getUid());

        this.mChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                                        @Override
                                                        public void onItemRangeInserted(int positionStart, int itemCount) {
                                                            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount()); // Scroll to bottom on new messages
                                                        }
                                                      });

        // Configures RecyclerView
        // Attaches the adapter to the RecyclerView to populate items
        this.mRecyclerView.setAdapter(this.mChatAdapter);

        // Sets layout manager to position the items
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Creates the options for the RecyclerView from a Query
     * @param query a Query object that contains the messages from Firestore of Firebase
     * @return a FirestoreRecyclerOptions<Message> object that contains the options for the Adapter
     */
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                                           .setQuery(query, Message.class)
                                           .setLifecycleOwner(this)
                                           .build();
    }
}
