package com.example.cp.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String Email;
    private FirebaseRecyclerAdapter<Product,ProductViewHolder> firebaseRecyclerAdapter;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference,databaseReference1;
    private GoogleSignInClient googleSignInClient;
    private GoogleApiClient googleApiClient;
    private ImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        authStateListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth1) {
                if(firebaseAuth1.getCurrentUser()==null){
                    //Toast.makeText(MainActivity.this,"in Listner "+ firebaseAuth1.getCurrentUser().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        firebaseUser=firebaseAuth.getCurrentUser();
        Email=firebaseUser.getEmail();

        recyclerView= findViewById(R.id.product_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference=FirebaseDatabase.getInstance().getReference().child("Product_List");
        databaseReference.keepSynced(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Snackbar.make(view , "Replace with your own action" , Snackbar.LENGTH_LONG)
                        .setAction("Action" , null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this , drawer , toolbar , R.string.navigation_drawer_open , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(databaseReference, Product.class)
                        .build();

        Product_FirebaseRecyclerAdapter product_firebaseRecyclerAdapter=new Product_FirebaseRecyclerAdapter(options);
        product_firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(product_firebaseRecyclerAdapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.order_list) {
            Intent intent=new Intent(MainActivity.this,Order_List_Activity.class);
            startActivity(intent);
        } else if (id == R.id.cart_list) {
            Intent intent=new Intent(MainActivity.this,Cart_List_Activity.class);
            startActivity(intent);
        } else if (id == R.id.settings) {
            Intent intent=new Intent(MainActivity.this,Setting_Activity.class);
            startActivity(intent);
        } else if (id == R.id.about) {
            Intent intent=new Intent(MainActivity.this,About_Activity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
// 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Are you sure ?").setTitle("Logout");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    firebaseAuth.signOut();
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    googleSignInClient=GoogleSignIn.getClient(MainActivity.this,gso);
                    googleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
