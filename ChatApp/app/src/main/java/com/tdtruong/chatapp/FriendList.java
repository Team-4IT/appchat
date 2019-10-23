package com.tdtruong.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendList extends AppCompatActivity {


    private View friendView;
    private RecyclerView myFriendList;

    private DatabaseReference ContactRef, UserRef;
    private FirebaseAuth mAuth;
    private String currenUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        myFriendList = findViewById(R.id.friend_list);
        myFriendList.setLayoutManager(new LinearLayoutManager(getBaseContext()));



        mAuth=FirebaseAuth.getInstance();
        currenUserID=mAuth.getCurrentUser().getUid();

        ContactRef =FirebaseDatabase.getInstance().getReference().child("Contact").child(currenUserID);
        UserRef=FirebaseDatabase.getInstance().getReference().child("User")

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(ContactRef, Contact.class)
                .build();
        final FirebaseRecyclerAdapter<Contact, contactViewHolder> adapter
                =new FirebaseRecyclerAdapter<Contact, contactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final contactViewHolder holder, int position, @NonNull Contact model) {
                String userIDs=getRef(position).getKey();

                UserRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("image")){
                            String userImage =dataSnapshot.child("image").getValue().toString();
                            String profileName =dataSnapshot.child("name").getValue().toString();
                            String profileStatus =dataSnapshot.child("userStatus").getValue().toString();

                            holder.userName.setText(profileName);
                            holder.userStatus.setText(profileStatus);
                            Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                        }
                        else
                        {
                            String profileName =dataSnapshot.child("name").getValue().toString();
                            String profileStatus =dataSnapshot.child("userStatus").getValue().toString();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public contactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

                  View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout,viewGroup,false);
                  contactViewHolder viewHolder= new contactViewHolder(view);
                  return viewHolder;
            }
        };

        myFriendList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class contactViewHolder extends RecyclerView.ViewHolder{


        TextView userName, userStatus;
        CircleImageView  profileImage;


        public contactViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.user_profile_image);

        }
    }
}