package com.mibtech.test.helper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.mibtech.test.R;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private SharedPreferences sharedPref;
    private static AppController mInstance;
    private com.android.volley.toolbox.ImageLoader mImageLoader;
    AppEnvironment appEnvironment;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        appEnvironment = AppEnvironment.SANDBOX;
        sharedPref = this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        //mMessage(SMS) Hash for auto read OTP sms
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        System.out.println("=====Application -> " + appSignatureHelper.getAppSignatures());

    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

    public static void setWindowFlag(final int bits, boolean on, Activity context) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    public static void TransparentStatus(Activity context) {


        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, context);
            context.getWindow().setStatusBarColor(Color.TRANSPARENT);
            //context.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    public void setData(String id, String value) {
        sharedPref.edit().putString(id, value).apply();
    }

    public String getData(String id) {
        return sharedPref.getString(id, "");
    }


    public String getDeviceToken() {
        return sharedPref.getString("DEVICETOKEN", "");
    }

    public void setDeviceToken(String token) {
        sharedPref.edit().putString("DEVICETOKEN", token).apply();
    }

    public Boolean getISLogin() {
        return sharedPref.getBoolean("islogin", false);
    }

    public void setLogin(Boolean islogin) {
        sharedPref.edit().putBoolean("islogin", islogin).apply();
    }

    public Boolean getIsVarified() {
        return sharedPref.getBoolean("isvarified", false);
    }

    public void setVarified(Boolean isvarified) {
        sharedPref.edit().putBoolean("isvarified", isvarified).apply();
    }

    public com.android.volley.toolbox.ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new BitmapCache());
        }
        return this.mImageLoader;
    }

    public static Boolean isConnected(final Activity activity) {
        Boolean check = false;
        ConnectivityManager ConnectionManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (networkInfo != null && networkInfo.isConnected() == true) {
            check = true;
        } else {
            Toast.makeText(activity, "Check Internet Connection..!!", Toast.LENGTH_SHORT).show();
           /* new AlertDialog.Builder(activity)
                    .setView(v)
                    .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isConnected(activity);
                        }
                    })
                    .show();*/
        }
        return check;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
