package com.mancel.yann.myfirebase.models.pojos;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Yann MANCEL on 27/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.models
 */
public class Message {

    // FIELDS --------------------------------------------------------------------------------------

    private String mMessage;
    private Date mDateCreated;
    private User mUserSender;
    private String mUrlImage;

    // CONSTRUCTORS --------------------------------------------------------------------------------

    /**
     * Initializes an Message object
     */
    public Message() {}

    /**
     * Initializes an Message object
     * @param message a String object that contains the message
     * @param userSender an User object that contains the user
     */
    public Message(String message, User userSender) {
        this.mMessage = message;
        this.mUserSender = userSender;
    }

    /**
     * Initializes an Message object
     * @param message a String object that contains the message
     * @param userSender an User object that contains the user
     * @param urlImage a String object that contains the Url of the image
     */
    public Message(String message, User userSender, String urlImage) {
        this.mMessage = message;
        this.mUserSender = userSender;
        this.mUrlImage = urlImage;
    }

    // METHODS -------------------------------------------------------------------------------------

    public String getMessage() {
        return this.mMessage;
    }
    @ServerTimestamp
    public Date getDateCreated() {
        return this.mDateCreated;
    }
    public User getUserSender() {
        return this.mUserSender;
    }
    public String getUrlImage() {
        return this.mUrlImage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }
    public void setDateCreated(Date dateCreated) {
        this.mDateCreated = dateCreated;
    }
    public void setUserSender(User userSender) {
        this.mUserSender = userSender;
    }
    public void setUrlImage(String urlImage) {
        this.mUrlImage = urlImage;
    }
}
