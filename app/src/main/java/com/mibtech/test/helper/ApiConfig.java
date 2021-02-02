package com.mibtech.test.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.mibtech.test.R;
import com.mibtech.test.activity.DrawerActivity;
import com.mibtech.test.model.OrderTracker;
import com.mibtech.test.model.PriceVariation;
import com.mibtech.test.model.Product;
import com.mibtech.test.model.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Key;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

public class ApiConfig {
    public static String user_location = "";
    public static double latitude1 = 0, longitude1 = 0;
    public static GPSTracker gps;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    static DecimalFormat decimalFormat = new DecimalFormat("#.##");


    public static String VolleyErrorMessage(VolleyError error) {
        String message = "";
        try {
            if (error instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!!";
            } else if (error instanceof AuthFailureError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ParseError) {
                message = "Parsing error! Please try again after some time!!";
            } else if (error instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            } else
                message = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress) {
        final ProgressDisplay progressDisplay = new ProgressDisplay(activity);

        if (AppController.isConnected(activity)) {
            if (isprogress)
                progressDisplay.showProgress();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //System.out.println("================= " + url + " == " + response);
                    callback.onSuccess(true, response);
                    if (isprogress)
                        progressDisplay.hideProgress();
                }

            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (isprogress)
                                progressDisplay.hideProgress();

                            callback.onSuccess(false, "");
                            String message = VolleyErrorMessage(error);
                            if (!message.equals(""))
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params1 = new HashMap<String, String>();
                    params1.put(Constant.AUTHORIZATION, "Bearer " + createJWT("eKart", "eKart Authentication"));
                    return params1;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    params.put(Constant.AccessKey, Constant.AccessKeyVal);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

    }

    public static String createJWT(String issuer, String subject) {
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            byte[] apiKeySecretBytes = Constant.JWT_KEY.getBytes();
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            JwtBuilder builder = Jwts.builder()
                    .setIssuedAt(now)
                    .setSubject(subject)
                    .setIssuer(issuer)
                    .signWith(signatureAlgorithm, signingKey);

            return builder.compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static boolean CheckValidattion(String item, boolean isemailvalidation, boolean ismobvalidation) {
        if (item.length() == 0)
            return true;
        else if (isemailvalidation && (!android.util.Patterns.EMAIL_ADDRESS.matcher(item).matches()))
            return true;
        else return ismobvalidation && (item.length() < 10 || item.length() > 12);
    }


    public static String GetDiscount(String oldprice, String newprice) {
        double dold = Double.parseDouble(oldprice);
        double dnew = Double.parseDouble(newprice);

        //return String.valueOf(((dnew / dold) - 1) * 100);
        return " (" + String.format("%.2f", (((dnew / dold) - 1) * 100)) + "%)";
    }

    public static ArrayList<Product> GetProductList(JSONArray jsonArray) {
        ArrayList<Product> productArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                    JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);

                    for (int j = 0; j < pricearray.length(); j++) {
                        JSONObject obj = pricearray.getJSONObject(j);
                        String discountpercent = "0", productPrice = " ";
                        if (obj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                            productPrice = obj.getString(Constant.PRICE);
                        else {
                            discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                            productPrice = obj.getString(Constant.DISCOUNTED_PRICE);
                        }
                        priceVariations.add(new PriceVariation(obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), productPrice, obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                    }
                    productArrayList.add(new Product(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES).toString(), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString("indicator")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productArrayList;
    }


    public static Product GetCartList(JSONArray jsonArray, String vid, String qty, DatabaseHelper databaseHelper) {
        Product product = null;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                    JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);
                    for (int j = 0; j < pricearray.length(); j++) {
                        JSONObject obj = pricearray.getJSONObject(j);
                        String discountpercent = "0", productPrice = "";

                        if (obj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                            productPrice = obj.getString(Constant.PRICE);
                        else {
                            discountpercent = GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                            productPrice = obj.getString(Constant.DISCOUNTED_PRICE);
                        }
                        priceVariations.clear();
                        if (obj.getString(Constant.ID).equals(vid)) {
                            if (obj.getString(Constant.SERVE_FOR).equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                                databaseHelper.DeleteOrderData(vid, obj.getString(Constant.PRODUCT_ID));
                            } else {
                                int quantity = Integer.parseInt(qty);
                                double totalprice = Double.parseDouble(decimalFormat.format(quantity * Double.parseDouble(productPrice)));
                                databaseHelper.UpdateOrderData(obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID),qty, totalprice, obj.getString(Constant.MEASUREMENT) +  obj.getString(Constant.MEASUREMENT_UNIT_ID) + "==" + jsonObject.getString(Constant.NAME) + "==" + productPrice.split("=")[0]);
                                priceVariations.add(new PriceVariation(obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), productPrice, obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent, quantity, totalprice));
                            }
                            break;
                        }
                    }
                    if (priceVariations.size() != 0) {
                        product = new Product(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES).toString(), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString("indicator"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public static void addMarkers(int currentPage, ArrayList<Slider> imglist, LinearLayout mMarkersLayout, Activity activity) {

        if (activity != null) {
            TextView[] markers = new TextView[imglist.size()];

            mMarkersLayout.removeAllViews();

            for (int i = 0; i < markers.length; i++) {
                markers[i] = new TextView(activity);
                markers[i].setText(Html.fromHtml("&#8226;"));
                markers[i].setTextSize(35);
                markers[i].setTextColor(activity.getResources().getColor(R.color.overlay_white));
                mMarkersLayout.addView(markers[i]);
            }
            if (markers.length > 0)
                markers[currentPage].setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        }
    }

    public static void setOrderTrackerLayout(Activity activity, OrderTracker order, RecyclerView.ViewHolder holder) {
        for (int i = 0; i < order.getOrderStatusArrayList().size(); i++) {
            int img = activity.getResources().getIdentifier("img" + i, "id", activity.getPackageName());
            int view = activity.getResources().getIdentifier("l" + i, "id", activity.getPackageName());
            int txt = activity.getResources().getIdentifier("txt" + i, "id", activity.getPackageName());
            int textview = activity.getResources().getIdentifier("txt" + i + "" + i, "id", activity.getPackageName());
            // System.out.println("===============" + img + " == " + view);
            View v = holder.itemView;
            if (img != 0 && v.findViewById(img) != null) {
                ImageView imageView = v.findViewById(img);
                imageView.setColorFilter(activity.getResources().getColor(R.color.colorAccent));
            }
            if (view != 0 && v.findViewById(view) != null) {
                View view1 = v.findViewById(view);
                view1.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
            }
            if (txt != 0 && v.findViewById(txt) != null) {
                TextView view1 = v.findViewById(txt);
                view1.setTextColor(activity.getResources().getColor(R.color.black));
            }
            if (textview != 0 && v.findViewById(textview) != null) {
                TextView view1 = v.findViewById(textview);
                String str = order.getOrderStatusArrayList().get(i).getStatusdate();
                String[] splited = str.split("\\s+");
                view1.setText(splited[0] + "\n" + splited[1]);
            }
        }
    }

    public static void SetFavOnImg(DatabaseHelper databaseHelper, ImageView imgFav, String id) {
        if (databaseHelper.getFavouriteById(id)) {
            imgFav.setImageResource(R.drawable.ic_favorite);
            imgFav.setTag("y");
        } else {
            imgFav.setImageResource(R.drawable.ic_favorite_not);
            imgFav.setTag("n");
        }

    }


    public static void AddRemoveFav(DatabaseHelper databaseHelper, ImageView imgFav, String id) {
        if (imgFav.getTag().equals("y")) {
            databaseHelper.removeFavouriteById(id);
            imgFav.setImageResource(R.drawable.ic_favorite_not);
            imgFav.setTag("n");
        } else {
            databaseHelper.addFavourite(id);
            imgFav.setImageResource(R.drawable.ic_favorite);
            imgFav.setTag("y");
        }
    }

    public static void setSnackBar(String message, String action, Activity activity) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();

            }
        });
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }


    public static Drawable buildCounterDrawable(int count, int backgroundImageId, Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        // System.out.println("=============count " + count);
        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setVisibility(View.VISIBLE);
            textView.setText("" + count);
        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void GetSettings(final Activity activity) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_SETTINGS, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                //  System.out.println("============pay method " + response);
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = objectbject.getJSONObject(Constant.SETTINGS);
                            Constant.VERSION_CODE = object.getString(Constant.KEY_VERSION_CODE);
                            Constant.REQUIRED_VERSION = object.getString(Constant.KEY_VERSION_CODE);
                            Constant.VERSION_STATUS = object.getString(Constant.KEY_UPDATE_STATUS);
                            Constant.REFER_EARN_BONUS = object.getString(Constant.KEY_REFER_EARN_BONUS);
                            Constant.REFER_EARN_ACTIVE = object.getString(Constant.KEY_REFER_EARN_STATUS);
                            Constant.REFER_EARN_METHOD = object.getString(Constant.KEY_REFER_EARN_METHOD);
                            Constant.REFER_EARN_ORDER_AMOUNT = object.getString(Constant.KEY_MIN_REFER_ORDER_AMOUNT);
                            Constant.MAX_EARN_AMOUNT = object.getString(Constant.KEY_MAX_EARN_AMOUNT);
                            Constant.MINIMUM_WITHDRAW_AMOUNT = Double.parseDouble(object.getString(Constant.KEY_MIN_WIDRAWAL));
                            Constant.SETTING_CURRENCY_SYMBOL = object.getString(Constant.CURRENCY);
                         try {
                             Constant.SETTING_TAX = Double.parseDouble(object.getString(Constant.TAX));
                         }catch (Exception e)
                         {

                             Log.e("TAG", "onSuccess: "+e.getMessage() );
                         }
                            Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble(object.getString(Constant.DELIEVERY_CHARGE));
                            Constant.SETTING_MAIL_ID = object.getString(Constant.REPLY_TO);
                            Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY = Double.parseDouble(object.getString(Constant.MINIMUM_AMOUNT));
                            Constant.ORDER_DAY_LIMIT = Integer.parseInt(object.getString(Constant.KEY_ORDER_RETURN_DAY_LIMIT));

                            if (DrawerActivity.tvWallet != null) {
                                DrawerActivity.tvWallet.setText(activity.getResources().getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                            }
                            String versionName = "";
                            try {
                                PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                                versionName = packageInfo.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (ApiConfig.compareVersion(versionName, Constant.VERSION_CODE) < 0) {
                                OpenBottomDialog(activity);
                            } else if (ApiConfig.compareVersion(versionName, Constant.REQUIRED_VERSION) < 0) {
                                OpenBottomDialog(activity);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, params, false);
    }

    public static void GetPaymentConfig(final Activity activity) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_PAYMENT_METHOD, Constant.GetVal);
        //  System.out.println("=====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        //    System.out.println("=====pay config " + response);
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = objectbject.getJSONObject(Constant.PAYMENT_METHODS);
                            Constant.MERCHANT_KEY = object.getString(Constant.PAY_M_KEY);
                            Constant.MERCHANT_ID = object.getString(Constant.PAYU_M_ID);
                            Constant.MERCHANT_SALT = object.getString(Constant.PAYU_SALT);
                            Constant.RAZOR_PAY_KEY_VALUE = object.getString(Constant.RAZOR_PAY_KEY);
                            Constant.PAYPAL = object.getString(Constant.paypal_method);
                            Constant.PAYUMONEY = object.getString(Constant.payu_method);
                            Constant.RAZORPAY = object.getString(Constant.razor_pay_method);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }

    public static void OpenBottomDialog(final Activity activity) {
        View sheetView = activity.getLayoutInflater().inflate(R.layout.lyt_terms_privacy, null);
        ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeAllViews();
        }

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        FrameLayout bottomSheet = (FrameLayout) mBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

        ImageView imgclose = sheetView.findViewById(R.id.imgclose);
        TextView txttitle = sheetView.findViewById(R.id.tvTitle);
        Button btnNotNow = sheetView.findViewById(R.id.btnNotNow);
        Button btnUpadateNow = sheetView.findViewById(R.id.btnUpdateNow);
        if (Constant.VERSION_STATUS.equals("0")) {
            btnNotNow.setVisibility(View.VISIBLE);
            imgclose.setVisibility(View.VISIBLE);
            mBottomSheetDialog.setCancelable(true);
        } else
            mBottomSheetDialog.setCancelable(false);


        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
            }
        });
        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
            }
        });

        btnUpadateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_LINK + activity.getPackageName())));
            }
        });
    }

    public static void displayLocationSettingsRequest(final Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;

                }
            }
        });
    }

    public static double getWalletBalance(final Activity activity, Session session) {

        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_USER_DATA, Constant.GetVal);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("=================*wallet " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR)) {
                            Constant.WALLET_BALANCE = Double.parseDouble(object.getString(Constant.KEY_BALANCE));
                            DrawerActivity.tvWallet.setText(activity.getResources().getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.USER_DATA_URL, params, false);
        return Constant.WALLET_BALANCE;
    }


    public static void getLocation(final Activity activity) {
        try {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user asynchronously -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    new AlertDialog.Builder(activity)
                            .setTitle(activity.getResources().getString(R.string.location_permission))
                            .setMessage(activity.getResources().getString(R.string.location_permission_message))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                                }
                            })
                            .create()
                            .show();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            } else {
                gps = new GPSTracker(activity);
                if (gps.canGetLocation()) {
                    user_location = gps.getAddressLine(activity);

                }
                if (gps.getIsGPSTrackingEnabled()) {
                    latitude1 = gps.latitude;
                    longitude1 = gps.longitude;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isGPSEnable(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;
    }


    public static String getAddress(double lat, double lng, Activity activity) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                address = add;
            }
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    public static int compareVersion(String version1, String version2) {
        String[] arr1 = version1.split("\\.");
        String[] arr2 = version2.split("\\.");

        int i = 0;
        while (i < arr1.length || i < arr2.length) {
            if (i < arr1.length && i < arr2.length) {
                if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])) {
                    return -1;
                } else if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])) {
                    return 1;
                }
            } else if (i < arr1.length) {
                if (Integer.parseInt(arr1[i]) != 0) {
                    return 1;
                }
            } else {
                if (Integer.parseInt(arr2[i]) != 0) {
                    return -1;
                }
            }

            i++;
        }

        return 0;
    }


}
