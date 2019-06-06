package com.mancel.yann.myfirebase.models.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.mancel.yann.myfirebase.models.pojos.User;

/**
 * Created by Yann MANCEL on 28/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.models
 */
public class UserRequests {

    // FIELDS --------------------------------------------------------------------------------------

    private static final String COLLECTION_NAME = "users";

    // METHODS (CRUD) ------------------------------------------------------------------------------

    // Collection Reference
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance()
                                .collection(COLLECTION_NAME);
    }

    // Create (user)
    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);

        return UserRequests.getUsersCollection()
                           .document(uid)
                           .set(userToCreate);
    }

    // Read (user)
    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserRequests.getUsersCollection()
                           .document(uid)
                           .get();
    }

    // Update (username)
    public static Task<Void> updateUsername(String uid, String username) {
        return UserRequests.getUsersCollection()
                           .document(uid)
                           .update("username", username);
    }

    // Update (isMentor)
    public static Task<Void> updateIsMentor(String uid, Boolean isMentor) {
        return UserRequests.getUsersCollection()
                           .document(uid)
                           .update("isMentor", isMentor);
    }

    // Delete (user)
    public static Task<Void> deleteUser(String uid) {
        return UserRequests.getUsersCollection()
                           .document(uid)
                           .delete();
    }
}
