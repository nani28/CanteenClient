package com.example.cp.user;

import android.support.annotation.NonNull;

public class Order{
    private String order_id;
    private String product_id;
    private String user_id;
    private String email;
    private String name;
    private String description;
    private String price;
    private String status;
    private String time;
    private String date;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image;
    public Order(String order_id,String product_id,String user_id,String email,String name,String description,String price,String status,String time,String date,String image){
        this.order_id=order_id;
        this.product_id=product_id;
        this.user_id=user_id;
        this.email=email;
        this.name=name;
        this.description=description;
        this.price=price;
        this.status=status;
        this.time=time;
        this.date=date;
        this.image=image;
    }
    public  Order(){

    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    @NonNull
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
