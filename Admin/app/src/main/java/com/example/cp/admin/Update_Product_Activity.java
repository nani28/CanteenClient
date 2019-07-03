package com.example.cp.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Update_Product_Activity extends AppCompatActivity{


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String Email;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private ImageButton product_image;
    private static final int GALLERY_REQUEST=1;
    private EditText product_name;
    private EditText product_description;
    private EditText product_price;
    private Button add_product;
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
        setContentView(R.layout.activity_update__product_);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent=new Intent(Update_Product_Activity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        authStateListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth1) {
                if(firebaseAuth1.getCurrentUser()==null){
                    //Toast.makeText(MainActivity.this,"in Listner "+ firebaseAuth1.getCurrentUser().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Update_Product_Activity.this,LoginActivity.class);
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
        add_product=findViewById(R.id.add_product);
        firebaseUser=firebaseAuth.getCurrentUser();
        storageReference=FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Product_List");
        progressDialog=new ProgressDialog(this);
        /*product_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });*/

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

                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(Update_Product_Activity.this,android.R.layout.simple_spinner_item,products);
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
                       product_name.setText(product.name);
                       product_description.setText(product.description);
                       product_price.setText(product.price);
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


        add_product.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                final String name=product_name.getText().toString().trim();
                final String description=product_description.getText().toString().trim();
                final String price=product_price.getText().toString().trim();
                final String image=product.image;
                if(TextUtils.isEmpty(name)){
                    Snackbar.make(v,"Product name is empty",Snackbar.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(description)){
                    Snackbar.make(v,"Product description is empty",Snackbar.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(price)){
                    Snackbar.make(v,"Product price is empty",Snackbar.LENGTH_LONG).show();
                }else {
                    if (uri != null) {
                        storageReference1 = storageReference.child("Product_Images").child(uri.getLastPathSegment());
                        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                                    @Override
                                    public void onSuccess(Uri uri1) {
                                        uri = uri1;
                                    }
                                });
                            }
                        });
                    } else {
                        uri = Uri.parse(image);
                    }
                    final Product p = new Product(product_id , name , description , price , uri.toString());
                    progressDialog.setMessage("Adding Product....");
                    progressDialog.show();
                    final DatabaseReference n = databaseReference.child(product_id);
                    n.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e("In second ",p.image);
                            n.setValue(p).addOnCompleteListener(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    uri = null;
                                    progressDialog.dismiss();
                                    Snackbar.make(v , "Product added successfully !!!" , Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode , int resultCode , @Nullable Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            uri=data.getData();
            product_image.setImageURI(uri);
        }
    }



}
