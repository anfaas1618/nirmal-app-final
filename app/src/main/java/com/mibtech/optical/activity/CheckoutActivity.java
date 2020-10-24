package com.mibtech.optical.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.mibtech.optical.R;
import com.mibtech.optical.helper.ApiConfig;
import com.mibtech.optical.helper.Constant;
import com.mibtech.optical.helper.DatabaseHelper;
import com.mibtech.optical.helper.PaymentModelClass;
import com.mibtech.optical.helper.Session;
import com.mibtech.optical.helper.VolleyCallback;
import com.mibtech.optical.model.Slot;

@SuppressLint("SetTextI18n")
public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback, PaymentResultListener {
    private String TAG = CheckoutActivity.class.getSimpleName();
    public Toolbar toolbar;
    public TextView tvTaxPercent, tvTaxAmt, tvDelivery, tvPayment, tvPowerType,tvLocation, tvAlert, tvWltBalance, tvCity, tvName, tvTotal, tvDeliveryCharge, tvSubTotal, tvCurrent, tvWallet, tvPromoCode, tvPCAmount, tvPlaceOrder, tvConfirmOrder, tvPreTotal,tvConfirmDelivery;
    LinearLayout lytPayOption, lytTax, lytOrderList, lytWallet, lytCLocation, paymentLyt,powerTypeLyt, deliveryLyt, lytPayU, lytPayPal, lytRazorPay, dayLyt;
    Button btnApply;
    EditText edtPromoCode;
    public ProgressBar prgLoading;
    Session session;
    JSONArray qtyList, variantIdList, nameList;
    DatabaseHelper databaseHelper;
    double total, subtotal;
    String deliveryCharge = "0";
    PaymentModelClass paymentModelClass;
    SupportMapFragment mapFragment;
    CheckBox chWallet, chHome, chWork;
    public RadioButton rToday, rTomorrow;

    String deliveryTime = "", deliveryDay = "", pCode = "", paymentMethod = "", label = "", appliedCode = "";
    RadioButton rbCod, rbPayU, rbPayPal, rbRazorPay;
    ProgressDialog mProgressDialog;
    RelativeLayout walletLyt, mainLayout;
    Map<String, String> razorParams;
    public String razorPayId;
    double usedBalance = 0;
    RecyclerView recyclerView;
    ArrayList<Slot> slotList;
    SlotAdapter adapter;
    ProgressBar pBar;
    public boolean isApplied;
    double taxAmt = 0.0;
    double dCharge = 0.0, pCodeDiscount = 0.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mainLayout = findViewById(R.id.mainLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        paymentModelClass = new PaymentModelClass(CheckoutActivity.this);
        databaseHelper = new DatabaseHelper(CheckoutActivity.this);
        session = new Session(CheckoutActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        pBar = findViewById(R.id.pBar);
        lytTax = findViewById(R.id.lytTax);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        dayLyt = findViewById(R.id.dayLyt);
        rbCod = findViewById(R.id.rbcod);
        rbPayU = findViewById(R.id.rbPayU);
        rbPayPal = findViewById(R.id.rbPayPal);
        rbRazorPay = findViewById(R.id.rbRazorPay);
        tvLocation = findViewById(R.id.tvLocation);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvPayment = findViewById(R.id.tvPayment);
        tvPowerType=findViewById(R.id.tvPowerType);
        tvPCAmount = findViewById(R.id.tvPCAmount);
        tvPromoCode = findViewById(R.id.tvPromoCode);
        tvAlert = findViewById(R.id.tvAlert);
        edtPromoCode = findViewById(R.id.edtPromoCode);
        lytPayPal = findViewById(R.id.lytPayPal);
        lytRazorPay = findViewById(R.id.lytRazorPay);
        lytPayU = findViewById(R.id.lytPayU);
        chWallet = findViewById(R.id.chWallet);
        chHome = findViewById(R.id.chHome);
        chWork = findViewById(R.id.chWork);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvTotal = findViewById(R.id.tvTotal);
        tvName = findViewById(R.id.tvName);
        tvCity = findViewById(R.id.tvCity);
        tvCurrent = findViewById(R.id.tvCurrent);
        lytPayOption = findViewById(R.id.lytPayOption);
        lytOrderList = findViewById(R.id.lytOrderList);
        lytCLocation = findViewById(R.id.lytCLocation);
        lytWallet = findViewById(R.id.lytWallet);
        walletLyt = findViewById(R.id.walletLyt);
        paymentLyt = findViewById(R.id.paymentLyt);
        powerTypeLyt=findViewById(R.id.powerTypeLyt);
        deliveryLyt = findViewById(R.id.deliveryLyt);
        tvWallet = findViewById(R.id.tvWallet);
        prgLoading = findViewById(R.id.prgLoading);
        tvPlaceOrder = findViewById(R.id.tvPlaceOrder);
        tvConfirmOrder = findViewById(R.id.tvConfirmOrder);
        tvConfirmDelivery=findViewById(R.id.tvConfirmDelivery);
        lytWallet.setVisibility(View.GONE);


        rToday = findViewById(R.id.rToday);
        rTomorrow = findViewById(R.id.rTomorrow);
        tvWltBalance = findViewById(R.id.tvWltBalance);
        tvPreTotal = findViewById(R.id.tvPreTotal);
        btnApply = findViewById(R.id.btnApply);
        tvLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_my_location, 0, 0, 0);
        tvCurrent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_address, 0, 0, 0);
        tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
        tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
        tvPowerType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
        tvConfirmOrder.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_confirm, 0);
        tvConfirmDelivery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_confirm, 0);
        tvPlaceOrder.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_process, 0);
        tvPreTotal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info, 0, 0, 0);
        ApiConfig.getWalletBalance(CheckoutActivity.this, session);
        GetTimeSlots();
        try {
            qtyList = new JSONArray(session.getData(Session.KEY_Orderqty));
            variantIdList = new JSONArray(session.getData(Session.KEY_Ordervid));
            nameList = new JSONArray(session.getData(Session.KEY_Ordername));

            for (int i = 0; i < nameList.length(); i++) {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setWeightSum(4f);
                String[] name = nameList.getString(i).split("==");
                TextView tv1 = new TextView(this);
                tv1.setText(name[1] + " (" + CartActivity.cartNames.get("" + i) + ")");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1.5f;
                tv1.setLayoutParams(lp);
                tv1.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                linearLayout.addView(tv1);

                TextView tv2 = new TextView(this);
                tv2.setText(qtyList.getString(i));
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.weight = 0.7f;
                tv2.setLayoutParams(lp1);
                tv2.setGravity(Gravity.CENTER);
                linearLayout.addView(tv2);

                TextView tv3 = new TextView(this);
                tv3.setText(Constant.SETTING_CURRENCY_SYMBOL + name[2]);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.weight = 0.8f;
                tv3.setLayoutParams(lp2);
                tv3.setGravity(Gravity.CENTER);
                linearLayout.addView(tv3);

                TextView tv4 = new TextView(this);
                tv4.setText(Constant.SETTING_CURRENCY_SYMBOL + name[3]);
                LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp3.weight = 1f;
                tv4.setLayoutParams(lp3);
                tv4.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                linearLayout.addView(tv4);
                lytOrderList.addView(linearLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SetDataTotal();
        chWallet.setTag("false");
        getWalletBalance();
        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);

        if (Constant.WALLET_BALANCE == 0) {
            chWallet.setEnabled(false);
            walletLyt.setEnabled(false);
        }
        chWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chWallet.getTag().equals("false")) {
                    chWallet.setChecked(true);
                    lytWallet.setVisibility(View.VISIBLE);

                    if (Constant.WALLET_BALANCE >= subtotal) {
                        usedBalance = subtotal;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + (Constant.WALLET_BALANCE - usedBalance));
                        paymentMethod = "wallet";
                        lytPayOption.setVisibility(View.GONE);
                    } else {
                        usedBalance = Constant.WALLET_BALANCE;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + "0.0");
                        lytPayOption.setVisibility(View.VISIBLE);
                    }
                    subtotal = (subtotal - usedBalance);
                    tvWallet.setText("-" + Constant.SETTING_CURRENCY_SYMBOL + usedBalance);
                    tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
                    chWallet.setTag("true");

                } else {
                    walletUncheck();
                }

            }
        });
        PromoCodeCheck();
        setPaymentMethod();
    }

    public void walletUncheck() {
        lytPayOption.setVisibility(View.VISIBLE);
        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
        lytWallet.setVisibility(View.GONE);
        chWallet.setChecked(false);
        chWallet.setTag("false");
        SetDataTotal();
    }

    public void setPaymentMethod() {
        if (Constant.PAYPAL.equals("1"))
            lytPayPal.setVisibility(View.VISIBLE);
        else
            lytPayPal.setVisibility(View.GONE);

        if (Constant.PAYUMONEY.equals("1"))
            lytPayU.setVisibility(View.VISIBLE);
        else
            lytPayU.setVisibility(View.GONE);

        if (Constant.RAZORPAY.equals("1"))
            lytRazorPay.setVisibility(View.VISIBLE);
        else
            lytRazorPay.setVisibility(View.GONE);


        chHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chHome.setChecked(true);
                chWork.setChecked(false);
                label = chHome.getTag().toString();
            }
        });
        chWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chWork.setChecked(true);
                chHome.setChecked(false);
                label = chWork.getTag().toString();
            }
        });
        rbCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbCod.setChecked(true);
                rbPayU.setChecked(false);
                rbPayPal.setChecked(false);
                rbRazorPay.setChecked(false);
                paymentMethod = rbCod.getTag().toString();

            }
        });
        rbPayU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPayU.setChecked(true);
                rbCod.setChecked(false);
                rbPayPal.setChecked(false);
                rbRazorPay.setChecked(false);
                paymentMethod = rbPayU.getTag().toString();

            }
        });

        rbPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPayPal.setChecked(true);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                rbRazorPay.setChecked(false);
                paymentMethod = rbPayPal.getTag().toString();

            }
        });

        rbRazorPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRazorPay.setChecked(true);
                rbPayPal.setChecked(false);
                rbCod.setChecked(false);
                rbPayU.setChecked(false);
                paymentMethod = rbRazorPay.getTag().toString();
                Checkout.preload(getApplicationContext());
            }
        });

    }

    private String getTime() {
        String delegate = "HH:mm aaa";
        return (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
    }


    public void SetDataTotal() {
        total = databaseHelper.getTotalCartAmt(session);
        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(total));
        subtotal = total;
        if (total <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY) {
            tvDeliveryCharge.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.SETTING_DELIVERY_CHARGE);
            subtotal = subtotal + Constant.SETTING_DELIVERY_CHARGE;
            deliveryCharge = Constant.SETTING_DELIVERY_CHARGE + "";
        } else {
            tvDeliveryCharge.setText(getResources().getString(R.string.free));
            deliveryCharge = "0";
        }
        taxAmt = ((Constant.SETTING_TAX * total) / 100);
        if (pCode.isEmpty()) {
            subtotal = (subtotal + taxAmt);
        } else
            subtotal = (subtotal + taxAmt - pCodeDiscount);
        tvTaxPercent.setText("Tax(" + Constant.SETTING_TAX + "%)");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(taxAmt));
        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + DatabaseHelper.decimalformatData.format(subtotal));
    }

    public void OnBtnClick(View view) {
        switch (view.getId()) {
            case R.id.tvConfirmDelivery:
                tvPowerType.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                tvPowerType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
                tvDelivery.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                deliveryLyt.setVisibility(View.GONE);
                tvConfirmDelivery.setVisibility(View.GONE);
                powerTypeLyt.setVisibility(View.VISIBLE);
                tvConfirmOrder.setVisibility(View.VISIBLE);
                //todo here add the logic to power of lens
                //todo price increment on lens
                break;
            case R.id.tvConfirmOrder:
                tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
                tvPowerType.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                tvPowerType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                tvConfirmOrder.setVisibility(View.GONE);
                tvPlaceOrder.setVisibility(View.VISIBLE);
                paymentLyt.setVisibility(View.VISIBLE);
                powerTypeLyt.setVisibility(View.GONE);

                break;
            case R.id.tvLocation:
                if (tvLocation.getTag().equals("hide")) {
                    tvLocation.setTag("show");
                    lytCLocation.setVisibility(View.VISIBLE);
                } else {
                    tvLocation.setTag("hide");
                    lytCLocation.setVisibility(View.GONE);
                }
                break;
            case R.id.tvPlaceOrder:
                PlaceOrderProcess();

                break;
            case R.id.imgedit:
                startActivity(new Intent(CheckoutActivity.this, ProfileActivity.class));
                break;
            case R.id.tvUpdate:
                if (ApiConfig.isGPSEnable(CheckoutActivity.this))
                    startActivity(new Intent(CheckoutActivity.this, MapActivity.class));
                else
                    ApiConfig.displayLocationSettingsRequest(CheckoutActivity.this);
                break;
            default:
                break;
        }
    }


    public void PlaceOrderProcess() {
        if (deliveryDay.length() == 0) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_delivery_day), Toast.LENGTH_SHORT).show();
            return;
        } else if (deliveryTime.length() == 0) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_delivery_time), Toast.LENGTH_SHORT).show();
            return;
        } else if (paymentMethod.isEmpty()) {
            Toast.makeText(CheckoutActivity.this, getString(R.string.select_payment_method), Toast.LENGTH_SHORT).show();
            return;
        }
        final Map<String, String> sendparams = new HashMap<String, String>();
        sendparams.put(Constant.PLACE_ORDER, Constant.GetVal);
        sendparams.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        sendparams.put(Constant.TAX_PERCENT, String.valueOf(Constant.SETTING_TAX));
        sendparams.put(Constant.TAX_AMOUNT, DatabaseHelper.decimalformatData.format(taxAmt));
        sendparams.put(Constant.TOTAL, DatabaseHelper.decimalformatData.format(total));
        sendparams.put(Constant.FINAL_TOTAL, DatabaseHelper.decimalformatData.format(subtotal));
        sendparams.put(Constant.PRODUCT_VARIANT_ID, String.valueOf(variantIdList));
        sendparams.put(Constant.QUANTITY, String.valueOf(qtyList));
        sendparams.put(Constant.MOBILE, session.getData(Session.KEY_MOBILE));
        sendparams.put(Constant.DELIVERY_CHARGE, deliveryCharge);
        sendparams.put(Constant.DELIVERY_TIME, (deliveryDay + " - " + deliveryTime));
        sendparams.put(Constant.KEY_WALLET_USED, chWallet.getTag().toString());
        sendparams.put(Constant.KEY_WALLET_BALANCE, String.valueOf(usedBalance));
        sendparams.put(Constant.PAYMENT_METHOD, paymentMethod);
        final String address = session.getData(Session.KEY_ADDRESS) + ", " + session.getData(Session.KEY_AREA) + ", " + session.getData(Session.KEY_CITY) + ", " + session.getData(Session.KEY_PINCODE) + ", Deliver to " + label;
        if (!pCode.isEmpty()) {
            sendparams.put(Constant.PROMO_CODE, pCode);
            sendparams.put(Constant.PROMO_DISCOUNT, String.valueOf(pCodeDiscount));
        }
        sendparams.put(Constant.ADDRESS, address);
        sendparams.put(Constant.LONGITUDE, session.getCoordinates(Session.KEY_LONGITUDE));
        sendparams.put(Constant.LATITUDE, session.getCoordinates(Session.KEY_LATITUDE));
        sendparams.put(Constant.EMAIL, session.getData(Session.KEY_EMAIL));
        System.out.println("=====params " + sendparams.toString());


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CheckoutActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_order_confirm, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final AlertDialog dialog = alertDialog.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvCancel, tvConfirm, tvItemTotal, tvTaxPercent1, tvTaxAmt1, tvDeliveryCharge1, tvTotal1, tvPromoCode1, tvPCAmount1, tvWallet1, tvFinalTotal1;
        LinearLayout lytPromo, lytWallet;

        lytPromo = dialogView.findViewById(R.id.lytPromo);
        lytWallet = dialogView.findViewById(R.id.lytWallet);
        tvItemTotal = dialogView.findViewById(R.id.tvItemTotal);
        tvTaxPercent1 = dialogView.findViewById(R.id.tvTaxPercent);
        tvTaxAmt1 = dialogView.findViewById(R.id.tvTaxAmt);
        tvDeliveryCharge1 = dialogView.findViewById(R.id.tvDeliveryCharge);
        tvTotal1 = dialogView.findViewById(R.id.tvTotal);
        tvPromoCode1 = dialogView.findViewById(R.id.tvPromoCode);
        tvPCAmount1 = dialogView.findViewById(R.id.tvPCAmount);
        tvWallet1 = dialogView.findViewById(R.id.tvWallet);
        tvFinalTotal1 = dialogView.findViewById(R.id.tvFinalTotal);
        tvCancel = dialogView.findViewById(R.id.tvCancel);
        tvConfirm = dialogView.findViewById(R.id.tvConfirm);
        String orderMessage = "";
        if (!pCode.isEmpty())
            lytPromo.setVisibility(View.VISIBLE);
        else
            lytPromo.setVisibility(View.GONE);

        if (chWallet.getTag().toString().equals("true"))
            lytWallet.setVisibility(View.VISIBLE);
        else
            lytWallet.setVisibility(View.GONE);

        dCharge = tvDeliveryCharge.getText().toString().equalsIgnoreCase("free") ? 0.0 : Constant.SETTING_DELIVERY_CHARGE;

        double totalAfterTax = (total + dCharge + taxAmt);
        tvItemTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + total);
        tvDeliveryCharge1.setText(tvDeliveryCharge.getText().toString());
        tvTaxPercent1.setText(getString(R.string.tax) + "(" + Constant.SETTING_TAX + "%) :");
        tvTaxAmt1.setText(tvTaxAmt.getText().toString());
        tvTotal1.setText(Constant.SETTING_CURRENCY_SYMBOL + totalAfterTax);
        tvPCAmount1.setText(tvPCAmount.getText().toString());
        tvWallet1.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + usedBalance);
        tvFinalTotal1.setText(Constant.SETTING_CURRENCY_SYMBOL + subtotal);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentMethod.equals(getResources().getString(R.string.codpaytype)) || paymentMethod.equals("wallet")) {
                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    System.out.println("====place order res " + response);
                                    JSONObject object = new JSONObject(response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        if (chWallet.getTag().toString().equals("true"))
                                            ApiConfig.getWalletBalance(CheckoutActivity.this, session);
                                        dialog.dismiss();
                                        startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //  System.out.println("========order=======" + response);
                        }
                    }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, sendparams, true);
                    dialog.dismiss();
                } else if (paymentMethod.equals(getString(R.string.pay_u))) {
                    dialog.dismiss();
                    sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_NAME));
                    paymentModelClass.OnPayClick(CheckoutActivity.this, sendparams);
                } else if (paymentMethod.equals(getString(R.string.paypal))) {
                    dialog.dismiss();
                    sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_NAME));
                    StartPayPalPayment(sendparams);
                } else if (paymentMethod.equals(getString(R.string.razor_pay))) {
                    dialog.dismiss();
                    sendparams.put(Constant.USER_NAME, session.getData(Session.KEY_NAME));
                    razorParams = sendparams;
                    CreateOrderId();

                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void CreateOrderId() {

        showProgressDialog(getString(R.string.loading));
        Map<String, String> params = new HashMap<>();
        String[] amount = String.valueOf(subtotal * 100).split("\\.");
        params.put("amount", "" + amount[0]);
        System.out.println("====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {

                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            startPayment(object.getString("id"), object.getString("amount"));
                            hideProgressDialog();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.Get_RazorPay_OrderId, params, false);

    }

    public void startPayment(String orderId, String payAmount) {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
        checkout.setImage(R.drawable.ic_launcher);

        try {
            JSONObject options = new JSONObject();
            options.put(Constant.NAME, session.getData(Session.KEY_NAME));
            options.put(Constant.ORDER_ID, orderId);
            options.put(Constant.CURRENCY, "INR");
            options.put(Constant.AMOUNT, payAmount);

            JSONObject preFill = new JSONObject();
            preFill.put(Constant.EMAIL, session.getData(Session.KEY_EMAIL));
            preFill.put(Constant.CONTACT, session.getData(Session.KEY_MOBILE));
            options.put("prefill", preFill);
            checkout.open(CheckoutActivity.this, options);
        } catch (Exception e) {
            Log.d(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            razorPayId = razorpayPaymentID;
            PlaceOrder(paymentMethod, razorPayId, true, razorParams, "Success");


        } catch (Exception e) {
            Log.d(TAG, "onPaymentSuccess  ", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "onPaymentError  ", e);
        }
    }

    public void PlaceOrder(final String paymentType, final String txnid, boolean issuccess, final Map<String, String> sendparams, final String status) {
        showProgressDialog(getString(R.string.processing));
        if (issuccess) {
            ApiConfig.RequestToVolley(new VolleyCallback() {
                @Override
                public void onSuccess(boolean result, String response) {

                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (!object.getBoolean(Constant.ERROR)) {
                                AddTransaction(object.getString(Constant.ORDER_ID), paymentType, txnid, status, getString(R.string.order_success), sendparams);
                                startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();

                            }
                            hideProgressDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, sendparams, false);
        } else {

            AddTransaction("", getString(R.string.razor_pay), txnid, status, getString(R.string.order_failed), sendparams);
        }
    }

    public void AddTransaction(String orderId, String paymentType, String txnid, final String status, String message, Map<String, String> sendparams) {
        Map<String, String> transparams = new HashMap<>();
        transparams.put(Constant.Add_TRANSACTION, Constant.GetVal);
        transparams.put(Constant.USER_ID, sendparams.get(Constant.USER_ID));
        transparams.put(Constant.ORDER_ID, orderId);
        transparams.put(Constant.TYPE, paymentType);
        transparams.put(Constant.TRANS_ID, txnid);
        transparams.put(Constant.AMOUNT, sendparams.get(Constant.FINAL_TOTAL));
        transparams.put(Constant.STATUS, status);
        transparams.put(Constant.MESSAGE, message);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        transparams.put(Constant.TXTN_DATE, df.format(c));

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            if (status.equals("Failed"))
                                finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.ORDERPROCESS_URL, transparams, false);
    }

    public void StartPayPalPayment(final Map<String, String> sendParams) {
        showProgressDialog(getString(R.string.processing));
        Map<String, String> params = new HashMap<>();
        params.put(Constant.FIRST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.LAST_NAME, sendParams.get(Constant.USER_NAME));
        params.put(Constant.PAYER_EMAIL, sendParams.get(Constant.EMAIL));
        params.put(Constant.ITEM_NAME, "Cart Order");
        params.put(Constant.ITEM_NUMBER, "1");
        params.put(Constant.AMOUNT, sendParams.get(Constant.FINAL_TOTAL));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                hideProgressDialog();
                Intent intent = new Intent(getApplicationContext(), PayPalWebActivity.class);
                intent.putExtra("url", response);
                intent.putExtra("params", (Serializable) sendParams);
                startActivity(intent);


            }
        }, CheckoutActivity.this, Constant.PAPAL_URL, params, true);
    }

    public void RefreshPromoCode(View view) {
        if (isApplied) {

            btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            btnApply.setText(getString(R.string.apply));
            edtPromoCode.setText("");
            tvPromoCode.setVisibility(View.GONE);
            tvPCAmount.setVisibility(View.GONE);
            isApplied = false;
            appliedCode = "";
            pCode = "";
            SetDataTotal();

        }
    }

    public void PromoCodeCheck() {
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String promoCode = edtPromoCode.getText().toString().trim();
                if (promoCode.isEmpty()) {
                    tvAlert.setVisibility(View.VISIBLE);
                    tvAlert.setText(getString(R.string.enter_promo_code));
                } else if (isApplied && promoCode.equals(appliedCode)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.promo_code_already_applied), Toast.LENGTH_SHORT).show();
                } else {
                    if (isApplied && !promoCode.equals(appliedCode)) {
                        SetDataTotal();
                    }
                    tvAlert.setVisibility(View.GONE);
                    btnApply.setVisibility(View.INVISIBLE);
                    pBar.setVisibility(View.VISIBLE);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Constant.VALIDATE_PROMO_CODE, Constant.GetVal);
                    params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
                    params.put(Constant.PROMO_CODE, promoCode);
                    params.put(Constant.TOTAL, String.valueOf(total));

                    ApiConfig.RequestToVolley(new VolleyCallback() {
                        @Override
                        public void onSuccess(boolean result, String response) {
                            if (result) {
                                try {
                                    JSONObject object = new JSONObject(response);
                                    //   System.out.println("===res " + response);
                                    if (!object.getBoolean(Constant.ERROR)) {
                                        pCode = object.getString(Constant.PROMO_CODE);
                                        tvPromoCode.setText(getString(R.string.promo_code) + "(" + pCode + ")");
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_green));
                                        btnApply.setText(getString(R.string.applied));
                                        isApplied = true;
                                        appliedCode = edtPromoCode.getText().toString();
                                        tvPCAmount.setVisibility(View.VISIBLE);
                                        tvPromoCode.setVisibility(View.VISIBLE);
                                        dCharge = tvDeliveryCharge.getText().toString().equalsIgnoreCase("free") ? 0.0 : Constant.SETTING_DELIVERY_CHARGE;
                                        subtotal = (Double.parseDouble(object.getString(Constant.DISCOUNTED_AMOUNT)) + taxAmt + dCharge);
                                        pCodeDiscount = (Double.parseDouble(object.getString(Constant.DISCOUNT)));
                                        tvPCAmount.setText("- " + Constant.SETTING_CURRENCY_SYMBOL + pCodeDiscount);
                                        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + subtotal);
                                    } else {
                                        btnApply.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                                        btnApply.setText(getString(R.string.apply));
                                        tvAlert.setVisibility(View.VISIBLE);
                                        tvAlert.setText(object.getString("message"));
                                    }
                                    pBar.setVisibility(View.GONE);
                                    btnApply.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, CheckoutActivity.this, Constant.PROMO_CODE_CHECK_URL, params, false);

                }
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null)
            paymentModelClass.TrasactionMethod(data, CheckoutActivity.this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;
        mMap.clear();
        LatLng latLng = new LatLng(Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE)), Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE)));
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(19));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapAsync(this);
        tvName.setText(session.getData(Session.KEY_NAME));
        tvCurrent.setText(getString(R.string.current_location) + " : " + ApiConfig.getAddress(Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE)), Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE)), CheckoutActivity.this));
        String address = session.getData(Session.KEY_ADDRESS) + ",<br>"
                + session.getData(Session.KEY_AREA)
                + ", " + session.getData(Session.KEY_CITY)
                + "- " + session.getData(Session.KEY_PINCODE)
                + "<br><b>" + getString(R.string.mobile_) + session.getData(Session.KEY_MOBILE);
        tvCity.setText(Html.fromHtml(address));
    }

    @Override
    public void onBackPressed() {

        if (paymentLyt.getVisibility() == View.VISIBLE) {
            walletUncheck();
            tvPayment.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            tvPayment.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process_gray, 0, 0, 0);
            tvDelivery.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            tvDelivery.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next_process, 0, 0, 0);
            tvConfirmOrder.setVisibility(View.VISIBLE);
            tvPlaceOrder.setVisibility(View.GONE);
            paymentLyt.setVisibility(View.GONE);
            deliveryLyt.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
    }

    public void getWalletBalance() {

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_USER_DATA, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("=================*setting " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            Constant.WALLET_BALANCE = Double.parseDouble(object.getString(Constant.KEY_BALANCE));
                            DrawerActivity.tvWallet.setText(getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                            tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.USER_DATA_URL, params, false);

    }

    public void GetTimeSlots() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_TIME_SLOT, Constant.GetVal);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        slotList = new ArrayList<>();
                        if (!object.getBoolean(Constant.ERROR)) {
                            dayLyt.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = object.getJSONArray(Constant.TIME_SLOTS);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object1 = jsonArray.getJSONObject(i);
                                slotList.add(new Slot(object1.getString(Constant.ID), object1.getString(Constant.TITLE), object1.getString(Constant.LAST_ORDER_TIME)));

                            }
                            adapter = new SlotAdapter(slotList);
                            recyclerView.setAdapter(adapter);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, CheckoutActivity.this, Constant.SETTING_URL, params, false);

    }


    public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.ViewHolder> {
        public ArrayList<Slot> categorylist;
        int selectedPosition = 0;

        public SlotAdapter(ArrayList<Slot> categorylist) {
            this.categorylist = categorylist;

        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_time_slot, parent, false);
            return new ViewHolder(view);
        }

        @NonNull
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            //holder.setIsRecyclable(false);
            final Slot model = categorylist.get(position);
            holder.rdBtn.setText(model.getTitle());
            holder.rdBtn.setTag(position);
            holder.rdBtn.setChecked(position == selectedPosition);
            if (deliveryDay.equals(getString(R.string.tomorrow))) {
                model.setSlotAvailable(true);
                // deliveryTime = model.getTitle();
            }
            if (model.isSlotAvailable()) {
                holder.rdBtn.setClickable(true);
                holder.rdBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            } else {
                holder.rdBtn.setChecked(false);
                holder.rdBtn.setClickable(false);
                holder.rdBtn.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            }
            if (getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()) > 0) {
                rToday.setClickable(false);
                rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
            } else {
                rToday.setClickable(true);
                rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            }
            System.out.println("======time slote valdation " + getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()));
            rToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getTime().compareTo(slotList.get(slotList.size() - 1).getLastOrderTime()) > 0) {
                        rToday.setClickable(false);
                        rToday.setChecked(false);
                        rToday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));

                    } else {
                        rToday.setChecked(true);
                        rTomorrow.setChecked(false);
                        deliveryDay = getString(R.string.today);
                        for (Slot s : slotList) {
                            if (getTime().compareTo(s.getLastOrderTime()) > 0) {
                                s.setSlotAvailable(false);
                            } else
                                s.setSlotAvailable(true);
                        }
                        notifyDataSetChanged();
                    }
                }
            });

            rTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deliveryDay = getString(R.string.tomorrow);
                    rToday.setChecked(false);
                    rTomorrow.setChecked(true);
                    notifyDataSetChanged();
                }

            });
            holder.rdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryTime = model.getTitle();
                    selectedPosition = (Integer) v.getTag();
                    notifyDataSetChanged();
                }
            });

            if (holder.rdBtn.isChecked()) {
                deliveryTime = model.getTitle();
            }
        }

        @Override
        public int getItemCount() {
            return categorylist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            RadioButton rdBtn;

            public ViewHolder(View itemView) {
                super(itemView);
                rdBtn = itemView.findViewById(R.id.rdBtn);

            }

        }
    }


}
