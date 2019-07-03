package com.example.cp.admin;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            holder.SetOrderId("Order Id : "+model.getOrder_id());
            holder.SetProductName("Name : "+model.getName());
            holder.SetDescription("Description : "+model.getDescription());
            holder.SetPrice("Price : "+model.getPrice());
            holder.SetStatus("Status : "+model.getStatus());
            holder.SetDate("Date : "+model.getDate());
            holder.SetTime("Time : "+model.getTime());
            holder.setCartProductImage(model.getImage());

    }

    @NonNull
    @Override
    public OrderRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup , int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_list_raw, viewGroup, false);
        return new OrderRecyclerViewHolder(view);
    }
}
