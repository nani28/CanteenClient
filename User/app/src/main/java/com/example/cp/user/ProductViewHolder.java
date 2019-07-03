package com.example.cp.user;

import android.arch.core.executor.DefaultTaskExecutor;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ProductViewHolder extends RecyclerView.ViewHolder{
    View view;
    static int totalprice=0;
    static boolean exist=false;
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
    public void setAddToCartButton(final String product_id){
        Button add_to_cart=view.findViewById(R.id.add_to_cart);
        add_to_cart.setText("Add to cart");
        add_to_cart.setTag(product_id);
        add_to_cart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                final String Email=firebaseUser.getEmail();
                final String user_id=firebaseUser.getUid();
                final String name =null;
                final String descritpion=null;
                final String price=null;
                final String image=null;
/*cart product list*/
                final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List");
/*user reference in cart added_list*/
                final DatabaseReference databaseReference1=databaseReference.child(user_id).child("cart_added_list");
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(product_id).exists()==true){
                            exist=true;
                        }
                        else{
                            exist=false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
/* user reference in cart*/
                final DatabaseReference foruser=databaseReference.child(user_id);
                foruser.child("email").setValue(Email);
                foruser.child("user_id").setValue(user_id);
                DatabaseReference databaseReference2=foruser.child("totalprice");
                databaseReference2.addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue(String.class)==null || dataSnapshot.exists()==false ) {
//                            Log.e("dataSnapshot.getValue",dataSnapshot.getValue(String.class));
                            totalprice=0;
                            foruser.child("totalprice").setValue("0");
                        }else {
                            Log.e("exist else condition ",dataSnapshot.getValue(String.class));
                            totalprice=Integer.parseInt(dataSnapshot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                /*user reference in cart added_list*/
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        DatabaseReference databaseReference2=FirebaseDatabase.getInstance().getReference().child("Product_List");
                        databaseReference2.child(product_id).addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Product p=dataSnapshot.getValue(Product.class);

                                DatabaseReference d=databaseReference1.child(product_id);
                                //d.child("name").setValue(p.name);
                                //d.child("description").setValue(p.description);
                                //d.child("price").setValue(p.price);
                                //d.child("image").setValue(p.image);
                                if(exist==false){
                                    int price=Integer.parseInt(p.price);
                                    int i=totalprice+price;
                                    String j= String.valueOf(i);
                                    foruser.child("totalprice").setValue(j);
                                }
                                d.child("id").setValue(p.id);
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
