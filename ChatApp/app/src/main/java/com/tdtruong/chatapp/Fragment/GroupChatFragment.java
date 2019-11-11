package com.tdtruong.chatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tdtruong.chatapp.GroupChatActivity;
import com.tdtruong.chatapp.MainActivity;
import com.tdtruong.chatapp.Model.User;
import com.tdtruong.chatapp.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupChatFragment extends Fragment {
    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();

    private DatabaseReference GroupRef;

    private FirebaseUser fuser;
    private DatabaseReference reference;

    private String imageURL;
    public GroupChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        IntializeFields();

        RetrieveAndDisplayGroups();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                imageURL = user.getImageURL();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupName = adapterView.getItemAtPosition(position).toString();


                MainActivity xxx = (MainActivity) getActivity();

                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName" , currentGroupName);
                groupChatIntent.putExtra("currentUserName", xxx.username.getText());
                groupChatIntent.putExtra("imageURL", imageURL);

                startActivity(groupChatIntent);
            }
        });
        return groupFragmentView;
    }


    private void IntializeFields() {
        list_view = groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_groups);
        list_view.setAdapter(arrayAdapter);
    }


    private void RetrieveAndDisplayGroups() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
