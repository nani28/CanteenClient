package com.example.cp.admin;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class Product_FirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Product,ProductViewHolder>{
    FirebaseRecyclerOptions<Product> options;
    public Product_FirebaseRecyclerAdapter(FirebaseRecyclerOptions options){
        super(options);
        this.options=options;

        // super(Product.class,R.layout.product_raw,ProductViewHolder.class,ref);

    }
    public Product_FirebaseRecyclerAdapter(FirebaseRecyclerOptions options,ArrayList<Product> productArrayList)
    {
        super(options);
        this.options=options;
    }
    @Override
    protected void onBindViewHolder(@NonNull ProductViewHolder holder , int position , @NonNull Product model) {
        holder.setProductName("Name         : "+model.getName());
        holder.setProductDescription("Description : "+model.getDescription());
        holder.setProductPrice("Price       : "+model.getPrice());
        holder.setProductImage(model.getImage());

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
