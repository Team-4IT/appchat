package com.tdtruong.chatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView mProfileImage;
    private TextView mName;
    private Toolbar mToolbar;
    private EditText mChatEditText;
    private ImageButton mFileButton;
    private ImageButton mSendButton;
    private ImageButton mEmotionButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mProfileImage = findViewById(R.id.profile_image);
        mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));


        mName = findViewById(R.id.name);
        mName.setText("Thọ Trịnh");

        mFileButton = findViewById(R.id.file_transfer_btn);
        mFileButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_file_transfer));

        mEmotionButton = findViewById(R.id.emotion_btn);
        mEmotionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_emotion));

        mSendButton = findViewById(R.id.send_btn);
        mSendButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_send));
    }
}