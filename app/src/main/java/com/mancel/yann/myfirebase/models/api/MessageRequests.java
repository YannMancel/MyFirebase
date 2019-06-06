package com.mancel.yann.myfirebase.models.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.mancel.yann.myfirebase.models.pojos.Message;
import com.mancel.yann.myfirebase.models.pojos.User;

/**
 * Created by Yann MANCEL on 28/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.models.api
 */
public class MessageRequests {

    // FIELDS --------------------------------------------------------------------------------------

    private static final String COLLECTION_NAME = "messages";

    // METHODS (CRUD) ------------------------------------------------------------------------------

    // Create (Message)
    public static Task<DocumentReference> createMessageForChat(String message, User userSender, String chat) {
        Message messageToCreate = new Message(message, userSender);

        return ChatRequests.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(messageToCreate);
    }

    // Create (Message with image)
    public static Task<DocumentReference> createMessageWithImageForChat(String message, User userSender, String urlImage, String chat) {
        Message messageToCreate = new Message(message, userSender, urlImage);

        return ChatRequests.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(messageToCreate);
    }

    // Read (Message)
    public static Query getAllMessageForChat(final String chat) {
        return ChatRequests.getChatCollection()
                           .document(chat)
                           .collection(COLLECTION_NAME)
                           .orderBy("dateCreated")
                           .limit(50);
    }
}
