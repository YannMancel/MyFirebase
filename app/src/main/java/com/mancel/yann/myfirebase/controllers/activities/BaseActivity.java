package com.mancel.yann.myfirebase.controllers.activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mancel.yann.myfirebase.R;

import butterknife.ButterKnife;

/**
 * Created by Yann MANCEL on 25/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.controllers.activities
 */
public abstract class BaseActivity extends AppCompatActivity {

    // METHODS -------------------------------------------------------------------------------------

    protected abstract int getActivityLayout();
    protected abstract void configureDesign();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associates the layout file to this class
        setContentView(this.getActivityLayout());

        // Using the ButterKnife library
        ButterKnife.bind(this);

        // configures the design of the activity
        this.configureDesign();
    }

    /**
     * Configures the ToolBar in adding the Up button
     */
    protected void configureToolBar() {
        // Gets a Support ActionBar object corresponding to this ToolBar
        ActionBar actionBar = getSupportActionBar();

        // Enables the Up Button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    // Firebase Authentication
    /**
     * Returns the current user of Firebase authentication
     * @return a FirebaseUser object that contains the current user
     */
    @Nullable
    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Firebase Authentication
    /**
     * Returns a true response if the user is logged
     * @return a boolean that contains the response if the user is logged
     */
    protected Boolean isCurrentUserLogged() {
        return this.getCurrentUser() != null;
    }

    // Firebase Firestore
    /**
     * Creates an OnFailureListener object to check if Firebase has been send an error when the CRUD actions
     * @return an OnFailureListener object
     */
    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Creates a Toast object which displays a message
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
            }
        };
    }
}
