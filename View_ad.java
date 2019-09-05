package com.example.libri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class View_ad extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private ImageView home, view_image;
    private TextView view_title, view_author,view_date, view_seller, view_price, view_condition,view_status ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ad);

        home = findViewById(R.id.libri_logo);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHome();
            }
        });


        view_title = findViewById(R.id.view_title);
        String title = getIntent().getStringExtra("title");
        view_title.setText(title);


        view_author = findViewById(R.id.view_author);
        String author = getIntent().getStringExtra("author");
        view_author.setText("by " + author);

        view_date = findViewById(R.id.view_date);
        String date = getIntent().getStringExtra("date");
        view_date.setText(date);

        /*view_seller = findViewById(R.id.view_seller);
        String seller = getIntent().getStringExtra("seller");
        view_seller.setText(seller);*/

        view_price = findViewById(R.id.view_price);
        String price = getIntent().getStringExtra("price");
        view_price.setText("R"+price);

        view_condition = findViewById(R.id.view_condition);
        String condition = getIntent().getStringExtra("condition");
        view_condition.setText(condition);

        view_status = findViewById(R.id.view_status);
        String status = getIntent().getStringExtra("status");
        view_status.setText(status);

        view_image = findViewById(R.id.view_image);
        String image = getIntent().getStringExtra("image");
       // Toast.makeText(this, "" + image, Toast.LENGTH_SHORT).show();
        Picasso.with(this)
                    .load(image)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    // .centerCrop()
                    .into(view_image);

        Button contact = findViewById(R.id.btn_contact_seller);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  connect to seller profile
            }
        });
        Button addToCart = findViewById(R.id.btn_add_to_cart);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  add the item to the cart
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
