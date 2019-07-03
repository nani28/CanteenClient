package com.example.cp.user;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by kamal_bunkar on 15-01-2019.
 */
public class checksum extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    private String order_id_list;
    private String total_price;
    private String mid;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private String user_id;
    private String Email,Email_removedot;
    private String key;
    private static final long twepoch = 1288834974657L;
    private static final long sequenceBits = 17;
    private static final long sequenceMax = 65536;
    private static volatile long lastTimestamp = -1L;
    private static volatile long sequence = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("paytm_order_id_map");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        user_id=firebaseUser.getUid();
        Email=firebaseUser.getEmail();
        Intent intent = getIntent();
        //order_id_list = intent.getExtras().getString("order_id_list");
        total_price=intent.getExtras().getString("total_price");
        Log.e("TOTAL PRICE",total_price);

        mid = "JzyqiI85886485116466"; /// your marchant key
        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
// vollye , retrofit, asynch
    }
    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(checksum.this);
        //private String orderId , mid, user_id, amt;

        String url ="https://cpsmarttech.000webhostapp.com/Canteen-Automation/generateChecksum.php";
        String varifyurl = //"https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
                "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(checksum.this);
            key=databaseReference.push().getKey();
            varifyurl+=key;

            String param= "MID="+mid+"&ORDER_ID=" +key+"&CUST_ID="+user_id+"&CHANNEL_ID=WAP"+"&TXN_AMOUNT="+total_price+"&WEBSITE=WEBSTAGING"+"&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
           final DatabaseReference totalpriceref=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List").child(user_id).child("totalprice");
            totalpriceref.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   // total_price=dataSnapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", key);
            paramMap.put("CUST_ID", user_id);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", total_price.toString());
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);
           // paramMap.put( "EMAIL" , Email);   // no need
            //paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(checksum.this, true, true,
                    checksum.this  );
        }
    }
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        String STATUS=bundle.getString("STATUS");
        final String TXNID=bundle.getString("TXNID");
        String BANKNAME=bundle.getString("BANKNAME");
        String ORDERID=bundle.getString("ORDERID");
        String TXNAMOUNT=bundle.getString("TXNAMOUNT");
        String TXNDATE=bundle.getString("TXNDATE");
        String PAYMENTMODE=bundle.getString("PAYMENTMODE");
        String BANKTXNID=bundle.getString("BANKTXNID");
        String CURRENCY=bundle.getString("CURRENCY");
        String GATEWAYNAME=bundle.getString("GATEWAYNAME");


        final DatabaseReference databaseReference1=databaseReference.child(key);
        databaseReference1.child("STATUS").setValue(STATUS);
        databaseReference1.child("TXNID").setValue(TXNID);
        databaseReference1.child("BANKNAME").setValue(BANKNAME);
        databaseReference1.child("ORDERID").setValue(ORDERID);
        databaseReference1.child("TXNAMOUNT").setValue(TXNAMOUNT);
        databaseReference1.child("TXNDATE").setValue(TXNDATE);
        databaseReference1.child("PAYMENTMODE").setValue(PAYMENTMODE);
        databaseReference1.child("BANKTXNID").setValue(BANKTXNID);
        databaseReference1.child("CURRENCY").setValue(CURRENCY);
        databaseReference1.child("GATEWAYNAME").setValue(GATEWAYNAME);

        /*final String[] order_list_split=order_id_list.split("_");
        for(int i=0;i<order_list_split.length;i++){
            databaseReference1.child(order_list_split[i]).child("id").setValue(order_list_split[i]);
        }*/
        final DatabaseReference product_list_databasereference=FirebaseDatabase.getInstance().getReference().child("Product_List");
        DatabaseReference cart_databasereference=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List").child(user_id).child("cart_added_list");
        final DatabaseReference place_order_totalprice=FirebaseDatabase.getInstance().getReference().child("Cart_Product_List").child(user_id).child("totalprice");
        final DatabaseReference order_databasereference=FirebaseDatabase.getInstance().getReference().child("Order_Product_List");
        cart_databasereference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()==true){
                    Log.e("IN IF","HELLLLLLLLLLLLLLLLLL");
                    for (DataSnapshot data:dataSnapshot.getChildren()) {
                        final String orderid = generateLongId().toString();
                        final Product p = data.getValue(Product.class);
                        product_list_databasereference.child(p.id).addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Date todayDate = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String todayString = formatter.format(todayDate);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                                Calendar calendar = Calendar.getInstance();
                                String time = dateFormat.format(calendar.getTime());
                                Product product = dataSnapshot.getValue(Product.class);
                                order_databasereference.child(orderid).child("TXTID").setValue(TXNID);
                                order_databasereference.child(orderid).child("order_id").setValue(orderid);
                                order_databasereference.child(orderid).child("product_id").setValue(product.id);
                                order_databasereference.child(orderid).child("name").setValue(product.name);
                                order_databasereference.child(orderid).child("description").setValue(product.description);
                                order_databasereference.child(orderid).child("image").setValue(product.image);
                                order_databasereference.child(orderid).child("price").setValue(product.price);
                                order_databasereference.child(orderid).child("status").setValue("Pending...");
                                order_databasereference.child(orderid).child("user_id").setValue(user_id);
                                order_databasereference.child(orderid).child("email").setValue(Email);
                                order_databasereference.child(orderid).child("date").setValue(todayString);
                                order_databasereference.child(orderid).child("time").setValue(time);
                                order_databasereference.child(orderid).child("payment_status").setValue("Pending...");
                                databaseReference1.child(orderid).child("id").setValue(orderid);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }else {
                    Log.e("IN ELSE","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Intent intent=new Intent(checksum.this,Order_List_Activity.class);
        intent.putExtra("Flag","Success");
        startActivity(intent);
    }
    @Override
    public void networkNotAvailable() {
    }
    @Override
    public void clientAuthenticationFailed(String s) {
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }
    @Override
    public void onBackPressedCancelTransaction() {
        Intent intent=new Intent(checksum.this,Cart_List_Activity.class);
        startActivity(intent);
        finish();
        Log.e("checksum ", " cancel call back respon  " );
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {

        Log.e("checksum ", "  transaction cancel " );
    }
    private static synchronized Long generateLongId() {
        long timestamp = System.currentTimeMillis();
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        Long id = ((timestamp - twepoch) << sequenceBits) | sequence;
        return id;
    }
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
