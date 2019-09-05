package com.example.libri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;


public class Create_Ad extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;

    private long advertId = 0;
    private Upload upload;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference tDatabaseRef;
    private StorageTask mUploadTask;

    private EditText ISBN, Title, Author, Faculty, Year, Price, Publisher;
    private Spinner Condition;
    private Button add_Image, post;
    private ImageView home, image;
    private ProgressBar progressBar;
    private Uri imageUri;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__ad);

        home = findViewById(R.id.libri_logo);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
            }
        });


        ISBN = findViewById(R.id.ISBN);
        Title = findViewById(R.id.Title);
        Author = findViewById(R.id.Author);
        Faculty = findViewById(R.id.Faculty);
        Condition = findViewById(R.id.Condition);
        Year = findViewById(R.id.Year);
        Price = findViewById(R.id.Price);
        Publisher = findViewById(R.id.Publisher);
        image = findViewById(R.id.image);

        add_Image = findViewById(R.id.Add_image_button);
        add_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        post = findViewById(R.id.post_button);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                   // Toast.makeText(Create_Ad.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else if (ISBN != null && Title != null && Author != null && Faculty != null && Condition != null && Year != null && Price != null && imageUri != null && Publisher != null){
                    try{
                        int year = Integer.parseInt(Year.getText().toString().trim());
                        try{
                            double price= Double.parseDouble(Price.getText().toString().trim());
                            new DecimalFormat("0.00").format(price);
                            uploadAd();
                        }catch(NumberFormatException e){
                            Toast.makeText(Create_Ad.this, "Please enter the price as a number eg.9.99", Toast.LENGTH_SHORT).show();
                        }
                    }catch(NumberFormatException e){
                        Toast.makeText(Create_Ad.this, "Please enter the year as a number eg.2019", Toast.LENGTH_SHORT).show();
                    }
                }


                else
                    Toast.makeText(Create_Ad.this, "Please fill in all of the fields and add a picture", Toast.LENGTH_SHORT).show();
            }
        });

        progressBar = findViewById(R.id.progress_bar);

        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("adverts");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    advertId = (dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tDatabaseRef = FirebaseDatabase.getInstance().getReference("textbooks");

        Spinner con = findViewById(R.id.Condition);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.conditions));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        con.setAdapter(adapter);
    }

    public void openHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openCart(){
        //opens cart activity

    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
           // Toast.makeText(this, "" + imageUri, Toast.LENGTH_SHORT).show();
            Picasso.with(this).load(imageUri).into(image);
            //mImageView.setImageURI(mImageUri); native

        } else Toast.makeText(this, " Retrieval Failed", Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadAd() {
        if (imageUri != null) {

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() +
                    "." + getFileExtension(imageUri));
            mUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 2000);
                            final String isbn = ISBN.getText().toString().trim();
                            String title = Title.getText().toString().trim();
                            String author = Author.getText().toString().trim();
                            String faculty = Faculty.getText().toString().trim();
                            String condition = Condition.getSelectedItem().toString();
                            String price =Price.getText().toString().trim();
                            String year = Year.getText().toString().trim();
                            String imageurl = fileReference.getDownloadUrl().toString();//this may be my image issue
                            String publisher = Publisher.getText().toString().trim();
                            String date = convertDate();

                            upload = new Upload(isbn, author, title, faculty, imageurl, price, year, condition, publisher, date);
                            mDatabaseRef.child(String.valueOf(advertId)).setValue(upload);
                            upload = new Upload(isbn,author,title,faculty,year,publisher);
                            tDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.hasChild("isbn"))
                                    tDatabaseRef.child(isbn).setValue(upload);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            advertId++;
                            Toast.makeText(Create_Ad.this, "Upload successful", Toast.LENGTH_SHORT).show();
                            openHome();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Create_Ad.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Create_Ad.this, "Upload in progress", Toast.LENGTH_LONG).show();
                            post.setClickable(false);
                            post.setBackgroundColor(getResources().getColor(R.color.red));
                            double progress = 100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount();
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    public String convertDate (){
        try{
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        return date.toString();
            }catch (Exception e){
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        } return "nd";
    }

    public void showPopUp(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_profile:
                Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_help:
                Toast.makeText(this, "Help selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_messages:
                Toast.makeText(this, "Messages selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_logout:
                Toast.makeText(this, "Logout selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }
}
