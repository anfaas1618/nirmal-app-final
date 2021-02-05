package com.mibtech.nirmalBakery.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import com.mibtech.nirmalBakery.R;
import com.mibtech.nirmalBakery.helper.AppController;
import com.mibtech.nirmalBakery.helper.Constant;
import com.mibtech.nirmalBakery.helper.PaymentModelClass;

public class PayPalWebActivity extends AppCompatActivity {


    Toolbar toolbar;
    WebView webView;
    String url;
    ProgressBar progressBar;
    PaymentModelClass paymentModelClass;
    boolean isTxnInProcess = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal_web);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PayPal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        paymentModelClass = new PaymentModelClass(PayPalWebActivity.this);
        url = getIntent().getStringExtra("url");

        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        progressBar.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                if (url.startsWith(Constant.MAINBASEUrl)) {
                    GetTransactionResponse(url);
                    return true;
                } else
                    isTxnInProcess = true;
                return false;
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    public void GetTransactionResponse(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        isTxnInProcess = false;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            if (status.equals("Completed"))
                                paymentModelClass.PlaceOrder(PayPalWebActivity.this, getString(R.string.paypal), jsonObject.getString("paypal_txn_id"), true, (Map<String, String>) getIntent().getSerializableExtra("params"), status);
                            else
                                paymentModelClass.PlaceOrder(PayPalWebActivity.this, getString(R.string.paypal), jsonObject.getString("paypal_txn_id"), false, (Map<String, String>) getIntent().getSerializableExtra("params"), status);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }


    @Override
    public void onBackPressed() {
        if (isTxnInProcess)
            ProcessAlertDialog();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void ProcessAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PayPalWebActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.txn_cancel_msg));

        alertDialog.setCancelable(false);
        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                paymentModelClass.PlaceOrder(PayPalWebActivity.this,
                        getString(R.string.paypal),
                        "none",
                        false,
                        (Map<String, String>) getIntent().getSerializableExtra("params"),
                        "canceled");

                alertDialog1.dismiss();
                finish();

            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
