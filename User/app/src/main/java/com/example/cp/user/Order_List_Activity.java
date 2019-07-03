package com.example.cp.user;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.ArrayList;
import java.util.List;

public class Order_List_Activity extends AppCompatActivity{
    private RecyclerView recycler_view;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String user_id;
    private String Email,Email_removedot;
    private List<Order> order_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__list_);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        user_id=firebaseUser.getUid();
        Email=firebaseUser.getEmail();
        order_list=new ArrayList<>();
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
        databaseReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                order_list.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Order o=dataSnapshot1.getValue(Order.class);
                    String u=o.getUser_id();
                    //Log.e("ORDER OBJECT IN ORDER",u);
                    if(user_id.equals(u)){
                        if(!order_list.contains(o)){
                            order_list.add(o);
                        }
                    }
                }
                Product_Order_RecycleAdapter product_order_recycleAdapter=new Product_Order_RecycleAdapter(order_list,Order_List_Activity.this);
                recycler_view.setAdapter(product_order_recycleAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.e("LIST ORDER  ",order_list.toString());
        /*DatabaseReference d=databaseReference.g;
        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(query, Order.class)
                        .build();
        Product_Order_FirebaseRecyclerAdapter product_order_firebaseRecyclerAdapter=new Product_Order_FirebaseRecyclerAdapter(options);
        product_order_firebaseRecyclerAdapter.startListening();
        recycler_view.setAdapter(product_order_firebaseRecyclerAdapter);*/
    }
}
