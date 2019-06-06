package com.mancel.yann.myfirebase.models.pojos;

import android.support.annotation.Nullable;

/**
 * Created by Yann MANCEL on 27/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.models
 */
public class User {

    // FIELDS --------------------------------------------------------------------------------------

    private String mUId;
    private String mUsername;
    private Boolean mIsMentor;
    @Nullable
    private String mUrlPicture;

    // CONSTRUCTORS --------------------------------------------------------------------------------

    /**
     * Initializes an User object
     */
    public User() {}

    /**
     * Initializes an User object
     * @param uId a String object that contains the UId value
     * @param username a String object that contains the username
     * @param urlPicture a String object that contains the Url of the picture
     */
    public User(String uId, String username, @Nullable String urlPicture) {
        this.mUId = uId;
        this.mUsername = username;
        this.mIsMentor = false;
        this.mUrlPicture = urlPicture;
    }

    // METHODS -------------------------------------------------------------------------------------

    public String getUId() {
        return this.mUId;
    }
    public String getUsername() {
        return this.mUsername;
    }
    public Boolean getIsMentor() {
        return this.mIsMentor;
    }
    @Nullable
    public String getUrlPicture() {
        return this.mUrlPicture;
    }

    public void setUId(String UId) {
        this.mUId = UId;
    }
    public void setUsername(String username) {
        this.mUsername = username;
    }
    public void setIsMentor(Boolean mentor) {
        this.mIsMentor = mentor;
    }
    public void setUrlPicture(@Nullable String urlPicture) {
        this.mUrlPicture = urlPicture;
    }
}
