package com.example.cp.user;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Product_Cart_FirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Product,CartRecyclerViewHolder>{
    public Product_Cart_FirebaseRecyclerAdapter(FirebaseRecyclerOptions options){
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull final CartRecyclerViewHolder holder , int position , @NonNull final Product model) {

        holder.setCartRemoveButton(model.id);
        Log.e("TAG",model.id);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Product_List").child(model.id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Product p= (Product) dataSnapshot.getValue(Product.class);
                Log.e("PRODUCT",dataSnapshot.getValue().toString());
                holder.setCartProductName(p.name);
                holder.setCartProductDescription(p.description);
                holder.setCartProductPrice(p.price);
                holder.setCartProductImage(p.image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public CartRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup , int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cart_list_raw, viewGroup, false);
        return new CartRecyclerViewHolder(view);
    }
}
