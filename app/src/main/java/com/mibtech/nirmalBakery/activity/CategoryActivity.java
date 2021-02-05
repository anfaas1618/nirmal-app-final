package com.mibtech.nirmalBakery.activity;


import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mibtech.nirmalBakery.R;
import com.mibtech.nirmalBakery.adapter.CategoryAdapter;
import com.mibtech.nirmalBakery.helper.Constant;

public class CategoryActivity extends AppCompatActivity {

    TextView txtnodata;
    RecyclerView categoryrecycleview;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.shopbycategory));

            txtnodata = findViewById(R.id.txtnodata);
            categoryrecycleview = findViewById(R.id.categoryrecycleview);
            categoryrecycleview.setLayoutManager(new GridLayoutManager(CategoryActivity.this, Constant.GRIDCOLUMN));
            if (MainActivity.categoryArrayList != null)
                if (MainActivity.categoryArrayList.size() == 0)
                    txtnodata.setVisibility(View.VISIBLE);
                else
                    categoryrecycleview.setAdapter(new CategoryAdapter(CategoryActivity.this, MainActivity.categoryArrayList, R.layout.lyt_category_main, "cate"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
