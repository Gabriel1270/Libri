package com.example.libri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

public class Initial_Buy extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, AdapterView.OnItemSelectedListener {
    private ImageButton search;
    private ImageView home, cart, menu;
    private String search_by, parameters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial__buy);

        search = (ImageButton) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainBuy();
            }
        });

        home = findViewById(R.id.libri_logo);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
            }
        });

        Spinner by = findViewById(R.id.search_by_button1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.spinner_item, getResources().getStringArray(R.array.book_field));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        by.setAdapter(adapter);
        by.setOnItemSelectedListener(this);

    }

    public void openMainBuy() {
        EditText search_parameters = findViewById(R.id.search_parameters);
        parameters = search_parameters.getText().toString().trim();
        Intent intent = new Intent(this, Main_Buy.class);
        intent.putExtra("search_parameters",parameters);
        intent.putExtra("search_by",search_by);
        startActivity(intent);

    }

    public void openHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openCart(){
        //opens cart activity

    }


    public void showPopUp(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()){
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
      search_by = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
