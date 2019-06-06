package com.mancel.yann.myfirebase.models.api;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Yann MANCEL on 28/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.models.api
 */
public class ChatRequests {

    // FIELDS --------------------------------------------------------------------------------------

    private static final String COLLECTION_NAME = "chats";

    // METHODS (CRUD) ------------------------------------------------------------------------------

    // Collection Reference
    public static CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance()
                                .collection(COLLECTION_NAME);
    }
}
