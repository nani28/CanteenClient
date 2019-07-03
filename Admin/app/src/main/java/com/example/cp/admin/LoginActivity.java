package com.example.cp.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity{

    private static final int RC_SIGN_IN=1;
    private static final String TAG="LoginActivity";
    private GoogleSignInClient googleSignInAccount;
    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    private GoogleSignInClient googleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        signInButton=(SignInButton)findViewById(R.id.signInButton);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInAccount=GoogleSignIn.getClient(this,gso);
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener(){
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    private void signIn() {
        Intent signInIntent = googleSignInAccount.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // Toast.makeText(this,"in FirebaseAuthwithGoogle",Toast.LENGTH_SHORT).show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Toast.makeText(LoginActivity.this,"in on isSuccessful",Toast.LENGTH_SHORT).show();

                            // Sign in success, update UI with the signed-in user's information



                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            String user_id=user.getUid();
                            FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference=firebaseDatabase.getReference("admin_list").child(user_id);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{

                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
                                        builder.setMessage("You are not Register as Admin ?").setTitle("Note");
                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                firebaseAuth.signOut();
                                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                        .requestIdToken(getString(R.string.default_web_client_id))
                                                        .requestEmail()
                                                        .build();
                                                googleSignInClient=GoogleSignIn.getClient(LoginActivity.this,gso);
                                                googleSignInClient.signOut().addOnCompleteListener(LoginActivity.this,
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                            }
                                                        });
                                            }
                                        });
// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            //checkUserExist();
                            //  progressDialog.dismiss();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }
}
