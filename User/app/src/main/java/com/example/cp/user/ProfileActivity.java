package com.example.cp.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity{

    private TextView profile_name;
    private TextView profile_email;
    private TextView profile_mobileno;
    private FirebaseDatabase firebaseDatabase;
    private ImageView profile_image;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth= FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        authStateListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth1) {
                if(firebaseAuth1.getCurrentUser()==null){
                    //Toast.makeText(MainActivity.this,"in Listner "+ firebaseAuth1.getCurrentUser().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        profile_email=findViewById(R.id.profile_email);
        profile_image=findViewById(R.id.profile_image);
        profile_mobileno=findViewById(R.id.profile_mobileno);
        profile_name=findViewById(R.id.profile_name);
        firebaseUser=firebaseAuth.getCurrentUser();
        profile_email.setText(firebaseUser.getEmail());
        profile_name.setText(firebaseUser.getDisplayName());
        Log.e("mobile_no",firebaseUser.getPhoneNumber());
        profile_mobileno.setText(firebaseUser.getPhoneNumber());
        Picasso.get().load(firebaseUser.getPhotoUrl().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image , new Callback(){
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError(Exception e) {
                Picasso.get().load(firebaseUser.getPhotoUrl().toString()).into(profile_image);
            }
        });


    }
}
