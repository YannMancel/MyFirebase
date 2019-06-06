package com.mancel.yann.myfirebase.views;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.mancel.yann.myfirebase.R;
import com.mancel.yann.myfirebase.models.pojos.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yann MANCEL on 29/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.views
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {

    // FIELDS --------------------------------------------------------------------------------------

    //ROOT VIEW
    @BindView(R.id.recycler_view_item_root_view) RelativeLayout mRootView;
    //PROFILE CONTAINER
    @BindView(R.id.recycler_view_item_profile_container) LinearLayout mProfileContainer;
    @BindView(R.id.recycler_view_item_profile_container_profile_image) ImageView mImageViewProfile;
    @BindView(R.id.recycler_view_item_profile_container_is_mentor_image) ImageView mImageViewIsMentor;
    //MESSAGE CONTAINER
    @BindView(R.id.recycler_view_item_message_container) RelativeLayout mMessageContainer;
    //IMAGE SENDED CONTAINER
    @BindView(R.id.recycler_view_item_message_container_image_sent_cardview) CardView mCardViewImageSent;
    @BindView(R.id.recycler_view_item_message_container_image_sent_cardview_image) ImageView mImageViewSent;
    //TEXT MESSAGE CONTAINER
    @BindView(R.id.recycler_view_item_message_container_text_message_container) LinearLayout mTextMessageContainer;
    @BindView(R.id.recycler_view_item_message_container_text_message_container_text_view) TextView mTextViewMessage;
    //DATE TEXT
    @BindView(R.id.recycler_view_item_message_container_text_view_date) TextView mTextViewDate;

    private final int mColorCurrentUser;
    private final int mColorRemoteUser;

    // CONSTRUCTORS --------------------------------------------------------------------------------

    /**
     * Initializes a MessageViewHolder object
      * @param itemView a View object
     */
    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        // Using the ButterKnife library
        ButterKnife.bind(this, itemView);

        this.mColorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
        this.mColorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
    }

    // METHODS -------------------------------------------------------------------------------------

    /**
     * Updates the item with the message
     * @param message a Message object that contains the message info
     * @param currentUserId a String object that contains the current user Id
     * @param glide a RequestManager object to connect with the Glide library
     */
    public void updateWithMessage(final Message message, final String currentUserId, final RequestManager glide) {
        // Checks if the current user is the sender
        final boolean isCurrentUser = message.getUserSender().getUId().equals(currentUserId);

        // Update message TextView
        this.mTextViewMessage.setText(message.getMessage());
        this.mTextViewMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END :
                                                               View.TEXT_ALIGNMENT_TEXT_START);

        // Update date TextView
        if (message.getDateCreated() != null) {
            this.mTextViewDate.setText(this.convertDateToHour(message.getDateCreated()));
        }

        // Update isMentor ImageView
        this.mImageViewIsMentor.setVisibility(message.getUserSender().getIsMentor() ? View.VISIBLE :
                                                                                      View.INVISIBLE);

        // Update profile picture ImageView
        if (message.getUserSender().getUrlPicture() != null) {
            glide.load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(this.mImageViewProfile);
        }

        // Update image sent ImageView
        if (message.getUrlImage() != null) {
            glide.load(message.getUrlImage())
                    .into(this.mImageViewSent);

            this.mImageViewSent.setVisibility(View.VISIBLE);
        }
        else {
            this.mImageViewSent.setVisibility(View.GONE);
        }

        //Update Message Bubble Color Background
        ((GradientDrawable) this.mTextMessageContainer.getBackground()).setColor(isCurrentUser ? this.mColorCurrentUser :
                                                                                                 this.mColorRemoteUser);

        // Update all views alignment depending is current user or not
        this.updateDesignDependingOnUser(isCurrentUser);
    }

    /**
     * Updates the layouts according the boolean value in argument
     * @param isSender a boolean that allows to know if the sender is the current user
     */
    private void updateDesignDependingOnUser(final boolean isSender) {
        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT :
                                              RelativeLayout.ALIGN_PARENT_LEFT);

        this.mProfileContainer.setLayoutParams(paramsLayoutHeader);

        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF :
                                               RelativeLayout.RIGHT_OF,
                                    R.id.recycler_view_item_profile_container);

        this.mMessageContainer.setLayoutParams(paramsLayoutContent);

        // CARDVIEW IMAGE SEND
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT :
                                           RelativeLayout.ALIGN_RIGHT,
                                R.id.recycler_view_item_message_container_text_message_container);

        this.mCardViewImageSent.setLayoutParams(paramsImageView);

        this.mRootView.requestLayout();
    }

    /**
     * Converts a Date object to a String object that contains the format [Hour:Minute]
     * @param date a Date object that contains the date to convert
     * @return a String object that contains the format [Hour:Minute]
     */
    private String convertDateToHour(final Date date) {
        // Creates a format [Hour:Minute]
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        return dateFormat.format(date);
    }
}
