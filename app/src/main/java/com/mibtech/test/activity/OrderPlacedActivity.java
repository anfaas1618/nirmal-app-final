package com.mibtech.optical.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mibtech.optical.R;

import com.mibtech.optical.helper.DatabaseHelper;

public class OrderPlacedActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        databaseHelper = new DatabaseHelper(OrderPlacedActivity.this);
        databaseHelper.DeleteAllOrderData();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnshopping) {
            startActivity(new Intent(OrderPlacedActivity.this, MainActivity.class));
            finishAffinity();
        } else if (id == R.id.txtsummary) {
            startActivity(new Intent(OrderPlacedActivity.this, OrderListActivity.class));
            finishAffinity();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
