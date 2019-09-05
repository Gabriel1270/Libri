package com.example.libri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Main_Buy extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
    private ImageView home;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressCircle;
    private ImageAdapter mAdapter;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;

    private String search_By, param;
    private EditText search_parameters2;

    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__buy);

        home = findViewById(R.id.libri_logo);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view_ads);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();
        mAdapter = new ImageAdapter(Main_Buy.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);

        mStorage = FirebaseStorage.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("adverts");

        String search_parameters = getIntent().getStringExtra("search_parameters");
        final String search_by = getIntent().getStringExtra("search_by");
        search(search_parameters,search_by);


        Spinner by = findViewById(R.id.search_by_button2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.book_field));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        by.setAdapter(adapter);
        by.setOnItemSelectedListener(this);
        search_parameters2 = findViewById(R.id.search_parameters2);

        final ImageButton search = findViewById(R.id.search_button2);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                param = search_parameters2.getText().toString().trim();
                search(param,search_By);
            }
        });



    }


    public void openHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openCart(){
        //opens cart activity

    }

    public void showSortPopUp(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_sort);//change to sort menu
        popup.show();
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
            case R.id.sort_alphaAZ:
                Collections.sort(mUploads, new Comparator<Upload>() {
                    @Override
                    public int compare(Upload upload, Upload t1) {
                        return upload.getTitle().compareTo(t1.getTitle());
                    }
                });
                mAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Ascending alphabetical sort completed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sort_alphaZA:
                Collections.sort(mUploads, new Comparator<Upload>() {
                    @Override
                    public int compare(Upload upload, Upload t1) {
                        return t1.getTitle().compareTo(upload.getTitle());
                    }
                });
                mAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Descending alphabetical sort completed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sort_priceLow:
                Collections.sort(mUploads, new Comparator<Upload>() {
                    @Override
                    public int compare(Upload upload, Upload t1) {
                        return Double.compare(Double.parseDouble(upload.getPrice()),Double.parseDouble(t1.getPrice()));
                    }
                });
                mAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Ascending price sort completed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sort_priceHigh:
                Collections.sort(mUploads, new Comparator<Upload>() {
                    @Override
                    public int compare(Upload upload, Upload t1) {
                        return Double.compare(Double.parseDouble(t1.getPrice()),Double.parseDouble(upload.getPrice()));
                    }
                });
                mAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Descending price sort completed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sort_date:
                Collections.sort(mUploads, new Comparator<Upload>() {
                    @Override
                    public int compare(Upload upload, Upload t1) {
                        return t1.getDate().compareTo(upload.getDate());
                    }
                });
                mAdapter.notifyDataSetChanged();

                Toast.makeText(this, "Most recent sort completed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        search_By = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void search(final String parameter, final String by){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();
                try{
                    int count =0;
                    String search_parameters = parameter;
                    String search_by = by;
                    //Toast.makeText(Main_Buy.this, "sp "+ search_parameters + "sb " + search_by, Toast.LENGTH_SHORT).show();
                    if(!search_parameters.equals(null)){
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Upload upload = postSnapshot.getValue(Upload.class);
                            switch (search_by){
                                case "Title":
                                    if((upload.getTitle().toLowerCase()).equals(search_parameters.toLowerCase())&& upload.getStatus().equals("Availible")) {
                                        mUploads.add(upload);
                                        count++;
                                        break;
                                    }
                                case "ISBN":
                                    if((upload.getISBN()).equals(search_parameters)){
                                        mUploads.add(upload);
                                        count++;
                                        break;
                                    }
                                case "Author":
                                    if((upload.getAuthor().toLowerCase()).equals(search_parameters.toLowerCase())&& upload.getStatus().equals("Availible")){
                                        mUploads.add(upload);
                                        count++;
                                        break;
                                    }
                                case "Faculty":
                                    if((upload.getFaculty().toLowerCase()).equals(search_parameters.toLowerCase())&& upload.getStatus().equals("Availible")){
                                        mUploads.add(upload);
                                        count++;
                                        break;
                                    }
                        /*case "Seller":
                            if((upload.getSeller().toLowerCase()).equals(search_parameters.toLowerCase())&& upload.getStatus().equals("Availible")){
                                mUploads.add(upload);
                                count++;
                                break;
                            }*/
                                default:
                                    break;
                            }
                        }
                    }
                    if(count<1){
                        Toast.makeText(Main_Buy.this, "No results found", Toast.LENGTH_LONG).show();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Upload upload = postSnapshot.getValue(Upload.class);
                            if(upload.getStatus().equals("Availible"))
                            mUploads.add(upload);
                        }
                    }
                }catch(Exception e){
                    Toast.makeText(Main_Buy.this, "Error : " + e, Toast.LENGTH_LONG).show();

                }
                // mAdapter = new ImageAdapter(Main_Buy.this, mUploads);
                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Main_Buy.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

        });

    }
}
