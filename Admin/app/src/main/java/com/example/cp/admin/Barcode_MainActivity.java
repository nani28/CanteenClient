package com.example.cp.admin;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Barcode_MainActivity extends AppCompatActivity implements View.OnClickListener{

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView order_id;
    private TextView name;
    private TextView description;
    private TextView price;
    private TextView status;
    private TextView date_order;
    private TextView time_order;
    private Spinner statusSpinner;
    private String order_id_ref;
    private String status_ref;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode__main);

        statusMessage = (TextView)findViewById(R.id.status_message);
        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        order_id=findViewById(R.id.product_order_id_scan);
        name=findViewById(R.id.product_order_name_scan);
        description=findViewById(R.id.product_order_description_scan);
        price=findViewById(R.id.product_order_price_scan);
        status=findViewById(R.id.product_order_status_scan);
        date_order=findViewById(R.id.product_order_date_scan);
        time_order=findViewById(R.id.product_order_time_scan);
        statusSpinner=findViewById(R.id.statusSpinner);
        final List<String> list = new ArrayList<String>();
        list.add("Pending...");
        list.add("Success");
        list.add("Failed");
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adp1);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent , View view , int position , long id) {
                status_ref= (String) parent.getSelectedItem();
                //Toast.makeText(Barcode_MainActivity.this,n.toString()+order_id.getText(),Toast.LENGTH_LONG).show();
                if(order_id_ref!=null){
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Order_Product_List").child(order_id_ref);
                    databaseReference.child("status").setValue(status_ref);
                    order_id_ref=null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.read_barcode).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    statusMessage.setText(R.string.barcode_success);
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Order_Product_List").child(barcode.displayValue);
                    databaseReference.addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Order order=dataSnapshot.getValue(Order.class);
                            Log.e("TAG ORDER ID",order.getOrder_id());
                            order_id.setText("Order ID : "+order.getOrder_id());
                            name.setText("Name : "+order.getName());
                            description.setText("Description : "+order.getDescription());
                            price.setText("Price : "+order.getPrice());
                            date_order.setText("Date : "+order.getDate());
                            time_order.setText("Time : "+order.getTime());
                            status.setText("Status : "+order.getStatus());
                            order_id_ref=order.getOrder_id();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
