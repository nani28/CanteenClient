package com.example.cp.user;

import java.util.HashMap;

public class Cart{
    public String user_id;
    public String email;

    public HashMap<String, String> getProduct_list() {
        return product_list;
    }

    public void setProduct_list(HashMap<String,String> product_list) {
        this.product_list = product_list;
    }

    public HashMap<String,String> product_list;
    public String totalprice;

    public Cart(String user_id,String email,String totalprice,HashMap<String,String> product_list){
        this.user_id=user_id;
        this.email=email;
        this.totalprice=totalprice;
        this.product_list=product_list;
    }

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

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }
}
