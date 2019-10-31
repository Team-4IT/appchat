package com.tdtruong.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tdtruong.chatapp.Model.Chat;
import com.tdtruong.chatapp.Model.User;
import com.tdtruong.chatapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;
    private List<Chat> mChat;
    private String imageUrl;

    private FirebaseUser fuser;


    public static final int MESS_LEFT = 0;
    public static final int MESS_RIGHT = 1;

    public MessageAdapter(Context context, List<Chat> chat, String imageUrl) {
        mContext = context;
        mChat = chat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MESS_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_content_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_content_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {
        Chat chat = mChat.get(position);
        String messageSenderID = fuser.getUid();
        String messageType = chat.getType();
        holder.chatContent.setText(chat.getMessage());

        if(imageUrl.equals("default"))
            holder.profileImage.setImageResource(R.drawable.profile_image);
        else
            Glide.with(mContext).load(imageUrl).into(holder.profileImage);

        holder.profileImage.setImageResource(R.drawable.profile_image);

        if(position == mChat.size() - 1) {
            if (chat.isIsseen())
                holder.seenMessage.setText("Seen");
            else
                holder.seenMessage.setText("Delivered");
            }
        else
            holder.seenMessage.setVisibility(View.GONE);

        if(messageType.equals("Text")){
            holder.fileImage.setVisibility(View.GONE);
            holder.chatContent.setVisibility(View.VISIBLE);
        }

        if(messageType.equals("File")){
            holder.fileImage.setVisibility(View.VISIBLE);
            holder.chatContent.setVisibility(View.GONE);
        }

        holder.fileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mChat.get(position).getMessage()));
                holder.fileImage.getContext().startActivity(intent);
            }
        });

        }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView chatContent;
        public ImageView profileImage,fileImage;
        public TextView seenMessage;


        public ViewHolder(View view) {
            super(view);
            chatContent = view.findViewById(R.id.chat_content);
            profileImage = view.findViewById(R.id.profile_image);
            seenMessage = view.findViewById(R.id.seen_message);
            fileImage = view.findViewById(R.id.img_file);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getUid_sender().equals(fuser.getUid()))
            return MESS_RIGHT;
        else
            return MESS_LEFT;
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }
}
