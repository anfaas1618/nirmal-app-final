package com.mibtech.nirmalBakery.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mibtech.nirmalBakery.R;
import com.mibtech.nirmalBakery.adapter.ProductAdapter;
import com.mibtech.nirmalBakery.helper.ApiConfig;
import com.mibtech.nirmalBakery.helper.Constant;
import com.mibtech.nirmalBakery.helper.DatabaseHelper;
import com.mibtech.nirmalBakery.helper.VolleyCallback;
import com.mibtech.nirmalBakery.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FavouriteActivity extends AppCompatActivity {

    TextView txtnodata;
    RecyclerView favrecycleview;
    DatabaseHelper databaseHelper;
    ArrayList<Product> productArrayList;
    ProductAdapter productAdapter;
    Toolbar toolbar;
    public RelativeLayout layoutSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_fav));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtnodata = findViewById(R.id.txtnodata);
        favrecycleview = findViewById(R.id.favrecycleview);
        layoutSearch = findViewById(R.id.layoutSearch);

        favrecycleview.setLayoutManager(new LinearLayoutManager(FavouriteActivity.this));
        databaseHelper = new DatabaseHelper(FavouriteActivity.this);
        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class).putExtra("from", Constant.FROMSEARCH));
            }
        });
        getData();
    }


    private void getData() {
        productArrayList = new ArrayList<>();
        if (databaseHelper.getFavourite().isEmpty()) {
            txtnodata.setVisibility(View.VISIBLE);
            favrecycleview.setAdapter(new ProductAdapter(productArrayList, R.layout.lyt_item_list, FavouriteActivity.this));
        } else {
            for (final String id : databaseHelper.getFavourite()) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.PRODUCT_ID, id);

                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        //  System.out.println("=================*bookmark- " + response);
                        if (result) {
                            try {
                                JSONObject objectbject = new JSONObject(response);
                                if (!objectbject.getBoolean(Constant.ERROR)) {
                                    JSONObject object = new JSONObject(response);
                                    JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                                    productArrayList.add(ApiConfig.GetProductList(jsonArray).get(0));
                                    productAdapter = new ProductAdapter(productArrayList, R.layout.lyt_item_list, FavouriteActivity.this);
                                    favrecycleview.setAdapter(productAdapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, FavouriteActivity.this, Constant.GET_PRODUCT_DETAIL_URL, params, false);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (productAdapter != null && !Constant.SELECTEDPRODUCT_POS.equals("")) {
            if (!databaseHelper.getFavouriteById(Constant.SELECTEDPRODUCT_POS.split("=")[1])) {
                productArrayList.remove(Integer.parseInt(Constant.SELECTEDPRODUCT_POS.split("=")[0]));
                productAdapter.notifyItemRemoved(Integer.parseInt(Constant.SELECTEDPRODUCT_POS.split("=")[0]));
                Constant.SELECTEDPRODUCT_POS = "";
            }
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
