package com.mibtech.optical.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import com.mibtech.optical.R;

import com.mibtech.optical.adapter.ItemsAdapter;
import com.mibtech.optical.helper.ApiConfig;
import com.mibtech.optical.helper.Constant;
import com.mibtech.optical.helper.Session;
import com.mibtech.optical.helper.VolleyCallback;
import com.mibtech.optical.model.OrderTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TrackerDetailActivity extends AppCompatActivity {

    OrderTracker order;
    TextView tvItemTotal, tvTaxPercent, tvTaxAmt, tvDeliveryCharge, tvTotal, tvPromoCode, tvPCAmount, tvWallet, tvFinalTotal, tvDPercent, tvDAmount;
    TextView txtcanceldetail, txtotherdetails, txtorderid, txtorderdate;
    NetworkImageView imgorder;
    Toolbar toolbar;
    public static ProgressBar pBar;
    RecyclerView recyclerView;
    public static Button btnCancel;
    public static LinearLayout lyttracker;
    View l4;
    LinearLayout returnLyt, lytPromo, lytWallet, lytPriceDetail;
    double totalAfterTax = 0.0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_detail);

        order = (OrderTracker) getIntent().getSerializableExtra("model");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pBar = findViewById(R.id.pBar);
        lytPriceDetail = findViewById(R.id.lytPriceDetail);
        lytPromo = findViewById(R.id.lytPromo);
        lytWallet = findViewById(R.id.lytWallet);
        tvItemTotal = findViewById(R.id.tvItemTotal);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvDAmount = findViewById(R.id.tvDAmount);
        tvDPercent = findViewById(R.id.tvDPercent);
        tvTotal = findViewById(R.id.tvTotal);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvPCAmount = findViewById(R.id.tvPCAmount);
        tvWallet = findViewById(R.id.tvWallet);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        txtorderid = findViewById(R.id.txtorderid);
        txtorderdate = findViewById(R.id.txtorderdate);


        imgorder = findViewById(R.id.imgorder);
        txtotherdetails = findViewById(R.id.txtotherdetails);
        txtcanceldetail = findViewById(R.id.txtcanceldetail);
        lyttracker = findViewById(R.id.lyttracker);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setNestedScrollingEnabled(false);
        btnCancel = findViewById(R.id.btncancel);
        l4 = findViewById(R.id.l4);
        returnLyt = findViewById(R.id.returnLyt);

        String[] date = order.getDate_added().split("\\s+");
        txtorderid.setText(order.getOrder_id());
        txtorderdate.setText(date[0]);
        txtotherdetails.setText(getString(R.string.name_1) + order.getUsername() + getString(R.string.mobile_no_1) + order.getMobile() + getString(R.string.address_1) + order.getAddress());
        totalAfterTax = (Double.parseDouble(order.getTotal()) + Double.parseDouble(order.getDelivery_charge()) + Double.parseDouble(order.getTax_amt()));

        tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getTotal());
        tvDeliveryCharge.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + order.getDelivery_charge());
        tvTaxPercent.setText(getString(R.string.tax) + "(" + order.getTax_percent() + "%) :");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + order.getTax_amt());
        tvDPercent.setText(getString(R.string.discount) + "(" + order.getdPercent() + "%) :");
        tvDAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + order.getdAmount());
        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + totalAfterTax);
        tvPCAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + order.getPromoDiscount());
        tvWallet.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + order.getWalletBalance());
        tvFinalTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + order.getFinal_total());
        if (!order.getStatus().equalsIgnoreCase("delivered") && !order.getStatus().equalsIgnoreCase("cancelled") && !order.getStatus().equalsIgnoreCase("returned")) {
            btnCancel.setVisibility(View.VISIBLE);
        } else {

            btnCancel.setVisibility(View.GONE);
        }
        if (order.getStatus().equalsIgnoreCase("cancelled")) {
            lyttracker.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            txtcanceldetail.setVisibility(View.VISIBLE);
            txtcanceldetail.setText(getString(R.string.canceled_on) + order.getStatusdate());
            lytPriceDetail.setVisibility(View.GONE);
        } else {
            lytPriceDetail.setVisibility(View.VISIBLE);
            if (order.getStatus().equals("returned")) {
                l4.setVisibility(View.VISIBLE);
                returnLyt.setVisibility(View.VISIBLE);
            }
            lyttracker.setVisibility(View.VISIBLE);
            for (int i = 0; i < order.getOrderStatusArrayList().size(); i++) {
                int img = getResources().getIdentifier("img" + i, "id", getPackageName());
                int view = getResources().getIdentifier("l" + i, "id", getPackageName());
                int txt = getResources().getIdentifier("txt" + i, "id", getPackageName());
                int textview = getResources().getIdentifier("txt" + i + "" + i, "id", getPackageName());

                // System.out.println("===============" + img + " == " + view);

                if (img != 0 && findViewById(img) != null) {
                    ImageView imageView = findViewById(img);
                    imageView.setColorFilter(getResources().getColor(R.color.colorPrimary));
                }

                if (view != 0 && findViewById(view) != null) {
                    View view1 = findViewById(view);
                    view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }

                if (txt != 0 && findViewById(txt) != null) {
                    TextView view1 = findViewById(txt);
                    view1.setTextColor(getResources().getColor(R.color.black));
                }

                if (textview != 0 && findViewById(textview) != null) {
                    TextView view1 = findViewById(textview);
                    String str = order.getDate_added();
                    String[] splited = str.split("\\s+");
                    view1.setText(splited[0] + "\n" + splited[1]);
                }
            }
        }
        recyclerView.setAdapter(new ItemsAdapter(TrackerDetailActivity.this, order.itemsList, "detail"));

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

    public void OnBtnClick(View view) {
        int id = view.getId();
        if (id == R.id.btncancel) {

            new AlertDialog.Builder(TrackerDetailActivity.this)
                    .setTitle(getString(R.string.cancel_order))
                    .setMessage(getString(R.string.cancel_msg))
                    .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(Constant.UPDATE_ORDER_STATUS, Constant.GetVal);
                            params.put(Constant.ID, order.getOrder_id());
                            params.put(Constant.STATUS, Constant.CANCELLED);
                            pBar.setVisibility(View.VISIBLE);
                            ApiConfig.RequestToVolley(new VolleyCallback() {
                                @Override
                                public void onSuccess(boolean result, String response) {
                                    // System.out.println("=================*cancelorder- " + response);
                                    if (result) {
                                        try {
                                            JSONObject object = new JSONObject(response);
                                            if (!object.getBoolean(Constant.ERROR)) {
                                                Constant.isOrderCancelled = true;
                                                finish();
                                                ApiConfig.getWalletBalance(TrackerDetailActivity.this, new Session(TrackerDetailActivity.this));
                                            }
                                            Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                                            pBar.setVisibility(View.GONE);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, TrackerDetailActivity.this, Constant.ORDERPROCESS_URL, params, false);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}
