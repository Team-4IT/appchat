package com.tdtruong.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdtruong.chatapp.Adapter.MessageAdapter;
import com.tdtruong.chatapp.Model.Chat;
import com.tdtruong.chatapp.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView mProfileImage;
    private TextView mName;
    private Toolbar mToolbar;

    private RecyclerView mRecyclerViewChat;

    private EditText mChatEditText;
    private ImageButton mFileButton;
    private ImageButton mSendButton;
    private ImageButton mEmotionButton;

    private FirebaseUser mUser;
    private DatabaseReference mReference;

    private List<Chat> mChatList;
    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;

    private Intent mIntent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = mIntent.getStringExtra("userid");


        mProfileImage = findViewById(R.id.profile_image);
        mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_image));


        mName = findViewById(R.id.name);
        mChatEditText = findViewById(R.id.chat_edit_text);


        mRecyclerView = findViewById(R.id.recycler_chat);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mFileButton = findViewById(R.id.file_transfer_btn);
        mFileButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_file_transfer));


        mEmotionButton = findViewById(R.id.emotion_btn);
        mEmotionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_emotion));


        mSendButton = findViewById(R.id.send_btn);
        mSendButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_send));
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = mChatEditText.getText().toString();
                if(!mess.equals(""))
                    sendMessage(mUser.getUid(), userid, mess);
                else
                    Toast.makeText(ChatActivity.this, "Your message is empty!",Toast.LENGTH_SHORT);
                mChatEditText.setText("");
            }
        });

        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mName.setText(user.getUsername());
                if(user.getImageURL().equals("default"))
                    mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_image));
                else
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(mProfileImage);

                ReadMessage(mUser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage(String sender, String receiver, String message){
        mReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        mReference.child("Chats").push().setValue(hashMap);
    }

    private void ReadMessage (final String myid, final String userid, final String imageurl){

        mChatList = new ArrayList<>();

        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChatList.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid))
                        mChatList.add(chat);

                    mMessageAdapter = new MessageAdapter(ChatActivity.this, mChatList, imageurl);
                    mRecyclerView.setAdapter(mMessageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
