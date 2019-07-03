package com.example.cp.admin;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Order_List_Activity extends AppCompatActivity{
    private RecyclerView recycler_view;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String user_id;
    private String Email,Email_removedot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__list_);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        user_id=firebaseUser.getUid();
        Email=firebaseUser.getEmail();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent=new Intent(Order_List_Activity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        authStateListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth1) {
                if(firebaseAuth1.getCurrentUser()==null){
                    //Toast.makeText(MainActivity.this,"in Listner "+ firebaseAuth1.getCurrentUser().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Order_List_Activity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recycler_view=findViewById(R.id.order_list_recyclerview);
        recycler_view.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setStackFromEnd(true);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Order_Product_List");


    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        Email_removedot=Email.replace(".","");
        final String orderid=user_id+Email_removedot;
        Query query=databaseReference.orderByChild("date");
        // Log.e("QUERY : ",query.toString());

        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(query, Order.class)
                        .build();
        Product_Order_FirebaseRecyclerAdapter product_order_firebaseRecyclerAdapter=new Product_Order_FirebaseRecyclerAdapter(options);
        product_order_firebaseRecyclerAdapter.startListening();
        recycler_view.setAdapter(product_order_firebaseRecyclerAdapter);
    }
}
