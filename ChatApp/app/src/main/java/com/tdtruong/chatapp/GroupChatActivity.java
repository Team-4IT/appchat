package com.tdtruong.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.text.format.Formatter;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tdtruong.chatapp.Adapter.MessageAdapter;
import com.tdtruong.chatapp.Model.Chat;
import com.tdtruong.chatapp.Model.GroupChat;
import com.tdtruong.chatapp.Model.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupChatActivity extends AppCompatActivity {

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    ImageButton btn_file_transfer;
    EditText text_send;

    MessageAdapter messageAdapter, listMessageAdapter;
    List<Chat> mchat;
    List<String> mimageURL;

    RecyclerView recyclerView;

    Intent intent;


    String groupName, currentUserName, imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroupChatActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send = findViewById(R.id.send_btn);
        btn_send.setImageResource(R.drawable.ic_action_send);

        btn_file_transfer = findViewById(R.id.file_transfer_btn);
        btn_file_transfer.setImageResource(R.drawable.ic_action_file_transfer);

        text_send = findViewById(R.id.chat_edit_text);

        intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        currentUserName = intent.getStringExtra("currentUserName");
        imageURL = intent.getStringExtra("imageURL");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    sendMessage(ip, groupName, msg);
                }
                else
                    Toast.makeText(GroupChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();

                text_send.setText("");
            }
        });

        btn_file_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("pdf/*");
                startActivityForResult(intent.createChooser(intent,"Send File"), 273);
            }
        });

        readMessage(groupName);

//        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                readMessage(groupName);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }


    private void sendMessage(String ipaddr_sender, final String groupName, String message){

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupName);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid_sender", fuser.getUid());
        hashMap.put("ipaddr_sender", ipaddr_sender);
        hashMap.put("user_name", currentUserName);
        hashMap.put("message", message);
        hashMap.put("type","Text");
        hashMap.put("imageURL",imageURL);

        reference.push().setValue(hashMap);
    }

    private void readMessage(final String groupname){
        mchat = new ArrayList<>();
        mimageURL = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupname);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    GroupChat groupChat = snapshot.getValue(GroupChat.class);
//                    mchat.add(groupChat);
                    Chat chat = snapshot.getValue(Chat.class);
                    User user = snapshot.getValue(User.class);

                    mimageURL.add(user.getImageURL());
                    mchat.add(chat);
                    messageAdapter = new MessageAdapter(GroupChatActivity.this, mchat, mimageURL);

                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}