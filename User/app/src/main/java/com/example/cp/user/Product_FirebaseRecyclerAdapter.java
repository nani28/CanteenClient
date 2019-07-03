package com.example.cp.user;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class Product_FirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Product,ProductViewHolder>{

    public Product_FirebaseRecyclerAdapter(FirebaseRecyclerOptions options){
        super(options);
        // super(Product.class,R.layout.product_raw,ProductViewHolder.class,ref);

    }
    public Product_FirebaseRecyclerAdapter(FirebaseRecyclerOptions options, ArrayList<Product> productArrayList)
    {
        super(options);

    }
    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder , int position , @NonNull Product model) {
        holder.setProductName("Name         : "+model.getName());
        holder.setProductDescription("Description : "+model.getDescription());
        holder.setProductPrice("Price       : "+model.getPrice());
        holder.setProductImage(model.getImage());
        holder.setAddToCartButton(model.id);

    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup , int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_raw, viewGroup, false);
        return new ProductViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

}
