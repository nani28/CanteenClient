package com.example.cp.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Cart_List_Activity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String Email,user_id,Email_removedot;
    private FirebaseRecyclerAdapter<Product,ProductViewHolder> firebaseRecyclerAdapter;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference,databaseReference_cart_user;
    private GoogleSignInClient googleSignInClient;
    private GoogleApiClient googleApiClient;
    private TextView tv_total;
    private Button placeOrder;
    private static final long twepoch = 1288834974657L;
    private static final long sequenceBits = 17;
    private static final long sequenceMax = 65536;
    private static volatile long lastTimestamp = -1L;
    private static volatile long sequence = 0L;
    private static String order_id_list;
    private static int total_price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart__list_);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent=new Intent(Cart_List_Activity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        tv_total=findViewById(R.id.tv_total);
        placeOrder=findViewById(R.id.placeOrder);
        authStateListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth1) {
                if(firebaseAuth1.getCurrentUser()==null){
                    //Toast.makeText(MainActivity.this,"in Listner "+ firebaseAuth1.getCurrentUser().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Cart_List_Activity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        firebaseUser=firebaseAuth.getCurrentUser();
        Email=firebaseUser.getEmail();
        user_id=firebaseUser.getUid();
        Email_removedot=Email.replace(".","");
        recyclerView= findViewById(R.id.product_cart_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List").child(user_id).child("cart_added_list");
        databaseReference.keepSynced(true);
        DatabaseReference totalpriceref=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List").child(user_id).child("totalprice");
        totalpriceref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String t=dataSnapshot.getValue(String.class);
                total_price=Integer.parseInt(t);
                if(t==null){
                    t="0";
                }
                tv_total.setText(t+" rs");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        placeOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final DatabaseReference product_list_databasereference=FirebaseDatabase.getInstance().getReference().child("Product_List");
                DatabaseReference cart_databasereference=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List").child(user_id).child("cart_added_list");
                final DatabaseReference place_order_totalprice=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List").child(user_id).child("totalprice");
                final DatabaseReference order_databasereference=FirebaseDatabase.getInstance().getReference().child("Order_Product_List");
                cart_databasereference.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()==true){
                            Intent intent=new Intent(Cart_List_Activity.this,checksum.class);
                            //intent.putExtra("order_id_list",order_id_list);
                             intent.putExtra("total_price",String.valueOf(total_price));

                           // total_price=0;
                            startActivity(intent);
                            finish();
                            // order_id_list=new String();
                           /* for (DataSnapshot data:dataSnapshot.getChildren()) {
                                final String orderid=generateLongId().toString();
                                order_id_list+=orderid+"_";
                                final Product p=data.getValue(Product.class);
                                product_list_databasereference.child(p.id).addListenerForSingleValueEvent(new ValueEventListener(){
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Date todayDate = Calendar.getInstance().getTime();
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                        String todayString = formatter.format(todayDate);
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                                        Calendar calendar = Calendar.getInstance();
                                        String time=dateFormat.format(calendar.getTime());
                                        Product product=dataSnapshot.getValue(Product.class);
                                        order_databasereference.child(orderid).child("order_id").setValue(orderid);
                                        order_databasereference.child(orderid).child("product_id").setValue(product.id);
                                        order_databasereference.child(orderid).child("name").setValue(product.name);
                                        order_databasereference.child(orderid).child("description").setValue(product.description);
                                        order_databasereference.child(orderid).child("image").setValue(product.image);
                                        order_databasereference.child(orderid).child("price").setValue(product.price);
                                        order_databasereference.child(orderid).child("status").setValue("Pending...");
                                        order_databasereference.child(orderid).child("user_id").setValue(user_id);
                                        order_databasereference.child(orderid).child("email").setValue(Email);
                                        order_databasereference.child(orderid).child("date").setValue(todayString);
                                        order_databasereference.child(orderid).child("time").setValue(time);
                                        order_databasereference.child(orderid).child("payment_status").setValue("Pending...");
                                        total_price+=Integer.parseInt(product.price);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            Intent intent=new Intent(Cart_List_Activity.this,checksum.class);
                            //intent.putExtra("order_id_list",order_id_list);
                            //intent.putExtra("total_price",String.valueOf(total_price));
                            //total_price=0;
                            startActivity(intent);*/

                        }else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Cart_List_Activity.this);
                            builder.setMessage("No product in cart !!!");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

    }
    private static synchronized Long generateLongId() {
        long timestamp = System.currentTimeMillis();
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        Long id = ((timestamp - twepoch) << sequenceBits) | sequence;
        return id;
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(databaseReference, Product.class)
                        .build();
        Product_Cart_FirebaseRecyclerAdapter product_cart_firebaseRecyclerAdapter=new Product_Cart_FirebaseRecyclerAdapter(options);
        product_cart_firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(product_cart_firebaseRecyclerAdapter);
    }
}
