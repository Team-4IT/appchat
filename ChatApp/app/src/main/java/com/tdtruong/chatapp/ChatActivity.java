package com.tdtruong.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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
//        mUser = FirebaseAuth.getInstance().getCurrentUser();
//        final String userid = mIntent.getStringExtra("userid");

        mProfileImage = findViewById(R.id.profile_image);


        mName = findViewById(R.id.name);
        mChatEditText = findViewById(R.id.chat_edit_text);


        mFileButton = findViewById(R.id.file_transfer_btn);
        mFileButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_file_transfer));

        mEmotionButton = findViewById(R.id.emotion_btn);
        mEmotionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_emotion));


        mSendButton = findViewById(R.id.send_btn);
        mSendButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_send));
//        mSendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String mess = mChatEditText.getText().toString();
//                if(!mess.equals(""))
//                    sendMessage(mUser.getUid(), userid, mess);
//                else
//                    Toast.makeText(ChatActivity.this, "Your message is empty!",Toast.LENGTH_SHORT);
//            }
//        });
    }

    private void sendMessage(String sender, String receiver, String message){
        mReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        mReference.child("Chats").push().setValue(hashMap);
    }
}
