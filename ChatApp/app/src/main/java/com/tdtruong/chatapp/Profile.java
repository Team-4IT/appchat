package com.tdtruong.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.transition.CircularPropagation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private Button UpdateAccountSetting;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserID ;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();


        InitializeFields();

        UpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSetting();
            }
        });
    }


    private void InitializeFields() {
        UpdateAccountSetting = findViewById(R.id.update_setting_button);
        userName = findViewById(R.id.set_user_name);
        userStatus =  findViewById(R.id.set_profile_status);
        userProfileImage=(CircleImageView) findViewById(R.id.set_profile_image);
    }

    private void UpdateSetting() {
        String setUserName=userName.getText().toString();
        String setStatus=userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this,"Please write your user name first...",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this,"Please write your status..",Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,String> profileMap= new HashMap<>();
                profileMap.put("uid",currentUserID);
                profileMap.put("name",setUserName);
                profileMap.put("status",setStatus);
             RootRef.child("User").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()) {
                         Toast.makeText(Profile.this, "Profile update successfuly...", Toast.LENGTH_SHORT).show();
                         Intent iOpenChatScreen = new Intent(Profile.this, ChatActivity.class);
                         startActivity(iOpenChatScreen);
                     } else {
                         String message = task.getException().toString();
                         Toast.makeText(Profile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                     }
                 }
             });
        }
    }
}


