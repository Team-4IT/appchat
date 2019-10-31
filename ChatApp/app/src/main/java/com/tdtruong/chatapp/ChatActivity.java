package com.tdtruong.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

    CircleImageView profile_image;
    TextView username;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    ImageButton btn_file_transfer;
    ImageButton btn_icon;
    ImageButton fileImage;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    String userid;
    String userIpAddress;

    private Uri fileUri;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


        recyclerView = findViewById(R.id.recycler_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        btn_send = findViewById(R.id.send_btn);
        btn_send.setImageResource(R.drawable.ic_action_send);

        btn_file_transfer = findViewById(R.id.file_transfer_btn);
        btn_file_transfer.setImageResource(R.drawable.ic_action_file_transfer);

        btn_icon = findViewById(R.id.emotion_btn);
        btn_icon.setImageResource(R.drawable.ic_action_emotion);

        text_send = findViewById(R.id.chat_edit_text);

        fileImage = findViewById(R.id.file);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        userIpAddress = intent.getStringExtra("useripaddr");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        loadingBar = new ProgressDialog(this);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            sendMessage(user.getIpaddress(), userIpAddress, msg);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }});
                }

                else if(fileImage.isShown()){
                    Log.e("Run","ok2");
                    sendMessageHasFile();
                    fileImage.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

                text_send.setText("");
            }
        });

        btn_file_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = text_send.getText().toString();
                if(!mess.equals(""))
                    sendMessage(fuser.getUid(), userid, mess);
                else
                    Toast.makeText(ChatActivity.this, "Your message is empty!",Toast.LENGTH_SHORT);
                text_send.setText("");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("pdf/*");
                startActivityForResult(intent.createChooser(intent,"Send File"), 273);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getUid_receiver().equals(fuser.getUid()) && chat.getUid_sender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String ipaddr_sender, final String ipaddr_receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ipaddr_sender", ipaddr_sender);
        hashMap.put("ipaddr_receiver", ipaddr_receiver);
        hashMap.put("uid_sender", fuser.getUid());
        hashMap.put("uid_receiver", userid);
        hashMap.put("message", message);
        hashMap.put("type","Text");
        hashMap.put("isseen", false);

        reference.child("Chat1").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist1")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist1")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());

    }

    private void sendMessageHasFile(){

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist1")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist1")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());
    }


    private void readMesagges(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chat1");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getUid_receiver().equals(myid) && chat.getUid_sender().equals(userid) ||
                            chat.getUid_receiver().equals(userid) && chat.getUid_sender().equals(myid)){
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 273 && resultCode == RESULT_OK && data.getData()!= null){
            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait, we are sending that file...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fileImage.setVisibility(View.VISIBLE);

            fileUri = data.getData();


            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(("Files"));
            final String messagePushID = fuser.getUid();
            final StorageReference filePath = storageReference.child(messagePushID+"."+data.getType());

            filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){

                        Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fileLink = uri.toString();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", fuser.getUid());
                                hashMap.put("receiver", userid);
                                hashMap.put("message", fileLink);
                                hashMap.put("type","File");
                                hashMap.put("isseen", false);
                                reference.child("Chat1").push().setValue(hashMap);
                                loadingBar.dismiss();

                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingBar.dismiss();
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double p =(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    loadingBar.setMessage((int)p + " % Uploading...");
                }
            });
        }
    }
}