package com.example.cp.admin;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class OrderRecyclerViewHolder extends RecyclerView.ViewHolder{
    private View view;
    public OrderRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view=itemView;
    }
    public void SetOrderId(String orderid){
        TextView order_id=view.findViewById(R.id.product_order_id);
        order_id.setText(orderid);
    }
    public void SetProductName(String name){
        TextView p_name=view.findViewById(R.id.product_order_name);
        p_name.setText(name);
    }
    public void SetDescription(String desc){
        TextView description=view.findViewById(R.id.product_order_description);
        description.setText(desc);
    }
    public void SetStatus(String status){
        TextView p_status=view.findViewById(R.id.product_order_status);
        p_status.setText(status);
    }
    public void SetPrice(String price){
        TextView p_price=view.findViewById(R.id.product_order_price);
        p_price.setText(price);
    }
    public void SetTime(String time){
        TextView p_time=view.findViewById(R.id.product_order_time);
        p_time.setText(time);
    }
    public void SetDate(String date){
        TextView p_date=view.findViewById(R.id.product_order_date);
        p_date.setText(date);
    }
    public void setCartProductImage(final String image){
        final ImageView imageView=view.findViewById(R.id.product_order_image);
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
}
