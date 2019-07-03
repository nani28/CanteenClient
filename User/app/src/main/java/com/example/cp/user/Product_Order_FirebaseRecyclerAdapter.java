package com.example.cp.user;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Product_Order_FirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Order,OrderRecyclerViewHolder>{
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String user_id;
    private String email;

    public Product_Order_FirebaseRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Order> options) {
        super(options);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        user_id=firebaseUser.getUid();
        email=firebaseUser.getEmail();
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderRecyclerViewHolder holder , int position , @NonNull Order model) {
        Log.e("USER_ID : ",model.getUser_id());
        Log.e("EMAIL : ",model.getEmail());
        Log.e("user_id : ",user_id);
        Log.e("email : ",email);
        if(model.getUser_id().equals(user_id) && model.getEmail().equals(email)){
            holder.SetOrderId("Order Id : "+model.getOrder_id());
            holder.SetProductName("Name : "+model.getName());
            holder.SetDescription("Description : "+model.getDescription());
            holder.SetPrice("Price : "+model.getPrice());
            holder.SetStatus("Status : "+model.getStatus());
            holder.SetDate("Date : "+model.getDate());
            holder.SetTime("Time : "+model.getTime());
            holder.setCartProductImage(model.getImage());
        }else {
           // holder.itemView.setSystemUiVisibility(View.GONE);
            holder.itemView.setVisibility(View.INVISIBLE);
            holder.itemView.setVisibility(View.AUTOFILL_TYPE_NONE);
        }
    }

    @NonNull
    @Override
    public OrderRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup , int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_list_raw, viewGroup, false);
        return new OrderRecyclerViewHolder(view);
    }
}
