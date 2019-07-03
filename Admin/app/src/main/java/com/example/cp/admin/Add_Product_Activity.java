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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class Add_Product_Activity extends AppCompatActivity{

    private ImageButton product_image;
    private static final int GALLERY_REQUEST=1;
    private EditText product_name;
    private EditText product_description;
    private EditText product_price;
    private Button add_product;
    private static Uri uri=null;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__product_);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intent=new Intent(Add_Product_Activity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        product_image=findViewById(R.id.product_image);
        product_name=findViewById(R.id.product_name);
        product_description=findViewById(R.id.product_description);
        product_price=findViewById(R.id.product_price);
        add_product=findViewById(R.id.add_product);
        firebaseUser=firebaseAuth.getCurrentUser();
        storageReference=FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Product_List");
        progressDialog=new ProgressDialog(this);
        product_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });

        add_product.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                final String name=product_name.getText().toString().trim();
                final String description=product_description.getText().toString().trim();
                final String price=product_price.getText().toString().trim();
                if(TextUtils.isEmpty(name)){
                    Snackbar.make(v,"Product name is empty",Snackbar.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(description)){
                    Snackbar.make(v,"Product description is empty",Snackbar.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(price)){
                    Snackbar.make(v,"Product price is empty",Snackbar.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(uri.toString().trim())){
                   // Snackbar.make(v,"Product image is not selected",Snackbar.LENGTH_LONG).show();
                }else {
                    progressDialog.setMessage("Adding Product....");
                    progressDialog.show();
                    final StorageReference storageReference1=storageReference.child("Product_Images").child(uri.getLastPathSegment());
                    storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri1) {
                                    final Uri downloadUri=uri1;
                                    final DatabaseReference n=databaseReference.push();
                                    n.addValueEventListener(new ValueEventListener(){
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            n.child("id").setValue(n.getKey());
                                            n.child("name").setValue(name);
                                            n.child("description").setValue(description);
                                            n.child("price").setValue(price);
                                            n.child("image").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>(){
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v,"Product added successfully !!!",Snackbar.LENGTH_LONG).show();
                                                    startActivity(new Intent(Add_Product_Activity.this,MainActivity.class));
                                                }
                                            });
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                    progressDialog.dismiss();
                                    Snackbar.make(v,"Product added successfully !!!",Snackbar.LENGTH_LONG).show();
                                    //Toast.makeText(PostActivity.this,"Upload Done!!!",Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(Add_Product_Activity.this,MainActivity.class));
                                }
                            });

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
