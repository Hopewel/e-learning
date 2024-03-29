package com.example.chatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chatting.Adapters.MessageAdapter;
import com.example.chatting.Models.AllMethods;
import com.example.chatting.Models.Message;
import com.example.chatting.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//import android.os.Bundle;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference messagedb;
    MessageAdapter messageAdapter;
    User u;
    List<Message> messages ;

    RecyclerView rvMesssage;
    EditText etMessage;
    ImageButton imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        init();

    }

    private void init(){

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        u = new User();

        rvMesssage = (RecyclerView)findViewById(R.id.rvMessage);
        etMessage = (EditText)findViewById(R.id.etMessage);
        imageButton = (ImageButton)findViewById(R.id.btnSend);
        imageButton.setOnClickListener(this);
        messages = new ArrayList<>();

    }

    @Override
    public void onClick(View view) {

        if (!TextUtils.isEmpty(etMessage.getText().toString())){

            Message message = new Message(etMessage.getText().toString(),u.getName());
            etMessage.setText("");
            messagedb.push().setValue(message);
        }
        else{
            Toast.makeText(getApplicationContext(),"You cannot send blank message",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuLogout){

            //auth.signOut();
            finish();
            startActivity(new Intent(GroupChatActivity.this,IndexActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = auth.getCurrentUser();

        u.setUid(currentUser.getUid());
        u.setEmail(currentUser.getEmail());

        database.getReference("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
                u.setUid(currentUser.getUid());
                AllMethods.name = u.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messagedb = database.getReference("message");
        messagedb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());
                messages.add(message);
                displayMessage(messages);
            }

            @Override
            public void onChildChanged(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());

                List<Message> newMessages = new ArrayList<Message>();

                for (Message m: messages){

                    if (m.getKey().equals(message.getKey())){
                        newMessages.add(message);
                    }
                    else{
                        newMessages.add(m);
                    }
                }
                messages = newMessages;

                displayMessage(messages);
            }

            @Override
            public void onChildRemoved(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);

                message.setKey(snapshot.getKey());

                List<Message> newMessages = new ArrayList<Message>();

                for (Message m:messages){
                    if(!m.getKey().equals(message.getKey())){
                        newMessages.add(m);
                    }
                }
                messages = newMessages;
                displayMessage(messages);
            }

            @Override
            public void onChildMoved(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        messages = new ArrayList<>();
    }

    private void displayMessage(List<Message> messages) {

        rvMesssage.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        messageAdapter = new MessageAdapter(GroupChatActivity.this,messages,messagedb);
        rvMesssage.setAdapter(messageAdapter);
    }
}