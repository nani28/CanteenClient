package com.example.cp.user;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
    public void setGenerateBarcode(final String order_id, final Context context){
        Button generate_barcode=view.findViewById(R.id.generate_barcode);
        generate_barcode.setTag(order_id);
        generate_barcode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e("Order ID:",order_id);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                LayoutInflater factory = LayoutInflater.from(context);
                final View view = factory.inflate(R.layout.barcode_dialog_image, null);
                ImageView imageView=view.findViewById(R.id.dialog_imageview);
                String text=order_id;
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,1000,1000);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                alertDialog.setView(view);
                alertDialog.setTitle("OR Code");
                alertDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                AlertDialog dialog = alertDialog.create();
                dialog.show();

            }
        });
    }
}
