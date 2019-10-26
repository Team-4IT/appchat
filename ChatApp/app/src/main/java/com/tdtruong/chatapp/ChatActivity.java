package com.tdtruong.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
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
    private String userid;

    private List<Chat> mChatList;
    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;

    private Intent mIntent;

    private Uri fileUri;
    private ProgressDialog loadingBar;
    private String checker="", myUrl="";
    private StorageTask uploadTask;

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

        mIntent = getIntent();
//        userid = mIntent.getStringExtra("userid");


        mProfileImage = findViewById(R.id.profile_image);
        mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_image));


        mName = findViewById(R.id.username);
        mChatEditText = findViewById(R.id.chat_edit_text);


        mRecyclerView = findViewById(R.id.recycler_chat);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mFileButton = findViewById(R.id.file_transfer_btn);
        mFileButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_file_transfer));
        mFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String mess = mChatEditText.getText().toString();
//                if(!mess.equals(""))
//                    sendMessage(mUser.getUid(), userid, mess);
//                else
//                    Toast.makeText(ChatActivity.this, "Your message is empty!",Toast.LENGTH_SHORT);
//                mChatEditText.setText("");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("pdf/*");
                startActivityForResult(intent.createChooser(intent,"Send File"), 273);
            }
        });


        mEmotionButton = findViewById(R.id.emotion_btn);
        mEmotionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_emotion));


        mSendButton = findViewById(R.id.send_btn);
        mSendButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_send));
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView mchatcontent = findViewById(R.id.chat_content);
                String mess = mChatEditText.getText().toString();
                if(!mess.equals(""))
//                    sendMessage(mUser.getUid(), userid, mess);
                    mchatcontent.setText(mess);
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
//                if(user.getImageURL().equals("default"))
//                    mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_image));
//                else
//                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(mProfileImage);

                mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_image));

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
//                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
//                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid))
//                        mChatList.add(chat);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 273 && resultCode == RESULT_OK && data.getData()!= null){
//            loadingBar.setTitle("Sending File");
//            loadingBar.setMessage("Please wait, we are sending that file...");
//            loadingBar.setCanceledOnTouchOutside(false);
//            loadingBar.show();

            fileUri = data.getData();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(("Files"));
//            final String messageSenderRef = "Messages/" + mUser.getUid() + "/";
//            final String messageReceiverRef = "Messages/";
            final String messagePushID = mUser.getUid();
            final StorageReference filePath = storageReference.child(messagePushID);
            filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
//                        loadingBar.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    loadingBar.dismiss();
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double p =(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
//                    loadingBar.setMessage((int)p + " % Uploading...");
                }
            });
        }
    }
}
