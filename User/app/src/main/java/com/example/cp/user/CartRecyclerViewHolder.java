package com.example.cp.user;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class CartRecyclerViewHolder extends RecyclerView.ViewHolder{
    private View view;
    static int totalprice=0,static_price=0;
    public Product p;
    public CartRecyclerViewHolder(View view){
        super(view);
        this.view=view;
    }

    public void  setCartProductName(String name){
        TextView product_cart_name=view.findViewById(R.id.product_cart_name);
        product_cart_name.setText("Name :"+name);
    }
    public void setCartProductDescription(String description){
        TextView product_cart_descrition=view.findViewById(R.id.product_cart_description);
        product_cart_descrition.setText("Description : "+description);
    }
    public void setCartProductPrice(String price){
        TextView product_cart_price=view.findViewById(R.id.product_cart_price);
        product_cart_price.setText("Price :"+price);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        final String Email=firebaseUser.getEmail();
        final String user_id=firebaseUser.getUid();
    }
    public void setCartProductImage(final String image){
        final ImageView imageView=view.findViewById(R.id.product_cart_image);
        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView , new Callback(){
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError(Exception e) {
                Picasso.get().load(image).into(imageView);
            }
        });
    }
    public void setCartRemoveButton(final String productid){
        Button button=view.findViewById(R.id.remove_to_cart);
        button.setTag(productid);
        button.setText("Remove");
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        final String Email=firebaseUser.getEmail();
        final String user_id=firebaseUser.getUid();

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List").child(user_id);
                final DatabaseReference databaseReference=databaseReference1.child("cart_added_list");
                final DatabaseReference forTotalPrice=databaseReference1.child("totalprice");
                forTotalPrice.addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(String.class)==null || dataSnapshot.exists()==false) {
                            totalprice=0;
                        }else {
                            totalprice=Integer.parseInt(dataSnapshot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                databaseReference.child(productid).addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {
                        DatabaseReference pData=FirebaseDatabase.getInstance().getReference().child("Product_List").child(productid);
                        pData.addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                p=dataSnapshot.getValue(Product.class);
                                Log.e("IN PRODUCT PRICE",p.price);
                                static_price=Integer.parseInt(p.price);
                                int pri=totalprice-static_price;
                                String j=String.valueOf(pri);
                                databaseReference1.child("totalprice").setValue(j);
                                static_price=0;
                                totalprice=0;
                                dataSnapshot1.getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
