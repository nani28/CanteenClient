package com.example.cp.admin;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProductViewHolder extends RecyclerView.ViewHolder{
    View view;
    public ProductViewHolder(View v){
        super(v);
        view=v;
    }
    public void setProductName(String productName){
        TextView name=(TextView)view.findViewById(R.id.product_name);
        name.setText(productName);
    }
    public void setProductDescription(String productDescription){
        TextView description=(TextView)view.findViewById(R.id.product_description);
        description.setText(productDescription);

    }
    public void setProductPrice(String productPrice){
        TextView price=(TextView)view.findViewById(R.id.product_price);
        price.setText(productPrice);
    }
    public void setProductImage(final String productImage){
        final ImageView imageView=view.findViewById(R.id.product_image);
        Picasso.get().load(productImage).networkPolicy(NetworkPolicy.OFFLINE).into(imageView , new Callback(){
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError(Exception e) {
                Picasso.get().load(productImage).into(imageView);
            }
        });
    }

    public void setDeleteButton(String id, final DatabaseReference databaseReference){
        Button button=view.findViewById(R.id.delete_product);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                databaseReference.getRef().removeValue();
            }
        });
    }
}
