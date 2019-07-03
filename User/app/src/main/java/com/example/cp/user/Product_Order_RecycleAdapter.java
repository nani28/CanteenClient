package com.example.cp.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class Product_Order_RecycleAdapter extends RecyclerView.Adapter<OrderRecyclerViewHolder>{

    View view;
    List<Order> list;
    Context context;
    public Product_Order_RecycleAdapter(List<Order> list, Context context)
    {
        this.list=list;
        this.context=context;
    }
    @NonNull
    @Override
    public OrderRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup , int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_list_raw,viewGroup,false);
        return new OrderRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRecyclerViewHolder orderRecyclerViewHolder , int i) {
        orderRecyclerViewHolder.SetOrderId("Order ID : "+list.get(i).getOrder_id());
        orderRecyclerViewHolder.SetProductName("Name : "+list.get(i).getName());
        orderRecyclerViewHolder.SetDescription("Description : "+list.get(i).getDescription());
        orderRecyclerViewHolder.SetStatus("Status : "+list.get(i).getStatus());
        orderRecyclerViewHolder.SetPrice("Price : "+list.get(i).getPrice());
        orderRecyclerViewHolder.SetTime("Time : "+list.get(i).getTime());
        orderRecyclerViewHolder.SetDate("Date : "+list.get(i).getDate());
        orderRecyclerViewHolder.setCartProductImage(list.get(i).getImage());
        orderRecyclerViewHolder.setGenerateBarcode(list.get(i).getOrder_id(),context);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}