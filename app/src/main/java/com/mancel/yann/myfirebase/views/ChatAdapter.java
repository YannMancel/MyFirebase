package com.mancel.yann.myfirebase.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.mancel.yann.myfirebase.R;
import com.mancel.yann.myfirebase.models.pojos.Message;

/**
 * Created by Yann MANCEL on 29/05/2019.
 * Name of the project: MyFirebase
 * Name of the package: com.mancel.yann.myfirebase.views
 */
public class ChatAdapter extends FirestoreRecyclerAdapter<Message, MessageViewHolder> {

    // INTERFACES ----------------------------------------------------------------------------------

    public interface MessageListerner {
        void onDataChanged();
    }

    // FIELDS --------------------------------------------------------------------------------------

    private RequestManager mGlide;
    private MessageListerner mMessageListener;
    private String mCurrentUserId;

    // CONSTRUCTORS --------------------------------------------------------------------------------

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options A FirestoreRecyclerOptions<Message> object
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, final RequestManager glide, final MessageListerner messageListener, final String currentUserId) {
        super(options);

        this.mGlide = glide;
        this.mMessageListener = messageListener;
        this.mCurrentUserId = currentUserId;
    }

    // METHODS -------------------------------------------------------------------------------------

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        // Update the item with the message
        holder.updateWithMessage(model, this.mCurrentUserId, this.mGlide);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Creates a Context object to the LayoutInflater object
        Context context = viewGroup.getContext();

        // Creates an inflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Creates the View thanks to the inflater
        View view = layoutInflater.inflate(R.layout.recycler_view_item, viewGroup, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.mMessageListener.onDataChanged();
    }
}
