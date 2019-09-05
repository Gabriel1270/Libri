package com.example.libri;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    private Button buy, sell;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buy = (Button)findViewById(R.id.button_buy);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBuy();
            }
        });
        sell = (Button)findViewById(R.id.button_sell);
        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSell();
            }
        });
    }

    public void openCart(){
        //opens cart activity

    }

    public void openBuy(){
        Intent intent = new Intent(this,Initial_Buy.class);
        startActivity(intent);

    }
    public void openSell(){
       Intent intent = new Intent(this,Create_Ad.class);
        startActivity(intent);

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
