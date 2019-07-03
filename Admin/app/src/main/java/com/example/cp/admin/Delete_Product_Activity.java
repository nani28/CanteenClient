package com.example.cp.admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Delete_Product_Activity extends AppCompatActivity{
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String Email;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private ImageView product_image;
    private static final int GALLERY_REQUEST=1;
    private TextView product_name;
    private TextView product_description;
    private TextView product_price;
    private Button product_delete;
    private static Uri uri=null;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private Spinner product_spinner;
    private HashMap<String,String> hashMap;
    private String product_id=null;
    private Product product=null;
    private StorageReference storageReference1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete__product_);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent=new Intent(Delete_Product_Activity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        authStateListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth1) {
                if(firebaseAuth1.getCurrentUser()==null){
                    //Toast.makeText(MainActivity.this,"in Listner "+ firebaseAuth1.getCurrentUser().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Delete_Product_Activity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        firebaseUser=firebaseAuth.getCurrentUser();
        Email=firebaseUser.getEmail();
        product_spinner=findViewById(R.id.product_spinner);
        product_image=findViewById(R.id.product_image);
        product_name=findViewById(R.id.product_name);
        product_description=findViewById(R.id.product_description);
        product_price=findViewById(R.id.product_price);
        product_delete=findViewById(R.id.product_delete);
        firebaseUser=firebaseAuth.getCurrentUser();
        storageReference=FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Product_List");
        progressDialog=new ProgressDialog(this);
        databaseReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> products = new ArrayList<String>();
                hashMap=new HashMap<String,String>();
                for(DataSnapshot productDataSnapshot:dataSnapshot.getChildren()){
                    String product_name=productDataSnapshot.child("name").getValue(String.class);
                    String product_id=productDataSnapshot.getKey();
                    products.add(product_name);
                    hashMap.put(product_id,product_name);
                }

                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(Delete_Product_Activity.this,android.R.layout.simple_spinner_item,products);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                product_spinner.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        product_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent , View view , int position , long id) {
                final String n= (String) parent.getSelectedItem();
                Set keys=hashMap.keySet();
                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    String key = (String) i.next();
                    String value = (String) hashMap.get(key);
                    if(value==n){
                        product_id=key;
                        break;
                    }
                }

                databaseReference.child(product_id).addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        product=dataSnapshot.getValue(Product.class);
                        Log.e("In first ",product.image);
                        product_name.setText("Product Name : "+product.name);
                        product_description.setText("Product Description : "+product.description);
                        product_price.setText("Product Price : "+product.price);
                        product_image.setImageURI(Uri.parse(product.image));
                        Picasso.get().load(Uri.parse(product.image)).networkPolicy(NetworkPolicy.OFFLINE).into(product_image , new Callback(){
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(Uri.parse(product.image)).into(product_image);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        product_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                databaseReference.child(product_id).addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Delete_Product_Activity.this);
                        builder.setMessage("Are you sure ?").setTitle("Delete Product");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dataSnapshot.getRef().removeValue();
                                Snackbar.make(v,"Product deleted successfully",Snackbar.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Snackbar.make(v,"Product not deleted ",Snackbar.LENGTH_LONG).show();

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
