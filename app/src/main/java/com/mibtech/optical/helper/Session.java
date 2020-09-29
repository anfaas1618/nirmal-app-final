package com.mibtech.optical.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mibtech.optical.activity.MainActivity;


public class Session {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public static final String PREFER_NAME = "eKart";
    public static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_ID = "id";
    public static final String KEY_FCM_ID = "fcm_id";
    public static final String KEY_EMAIL = "txtemail";
    public static final String KEY_MOBILE = "mobileno";
    public static final String KEY_NAME = "name";
    public static final String KEY_DOB = "dob";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_CITY = "city";
    public static final String KEY_AREA = "area";
    public static final String KEY_CITY_ID = "city_id";
    public static final String KEY_AREA_ID = "area_id";
    public static final String KEY_PINCODE = "pincode";
    public static final String KEY_STATUS = "status";
    public static final String KEY_CREATEDAT = "createdat";
    public static final String KEY_APIKEY = "apikey";
    public static final String KEY_Password = "password";
    public static final String KEY_Orderqty = "listqty";
    public static final String KEY_Ordervid = "listvid";
    public static final String KEY_Ordername = "listname";
    public static final String KEY_REFER_CODE = "refer_code";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_MERCHANT_KEY = "merchant_key";
    public static final String KEY_MERCHANT_ID = "merchant_id";
    public static final String KEY_MERCHANT_SALT = "salt_key";


    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getData(String id) {
        return pref.getString(id, "");
    }

    public String getCoordinates(String id) {
        return pref.getString(id, "0");
    }

    public void setData(String id, String val) {
        editor.putString(id, val);
        editor.commit();
    }

    public void setLogin(String id, Boolean val) {
        editor.putBoolean(id, val);
        editor.commit();
    }

    public void createUserLoginSession(String fcmId, String id, String name, String email, String mobile, String dob, String city, String area, String cityId, String areaId, String address, String pincode, String status, String createdat, String apikey, String password, String referCode, String latitude, String longitude) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_FCM_ID, fcmId);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_PINCODE, pincode);
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_CREATEDAT, createdat);
        editor.putString(KEY_APIKEY, apikey);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_AREA, area);
        editor.putString(KEY_CITY_ID, cityId);
        editor.putString(KEY_AREA_ID, areaId);
        editor.putString(KEY_Password, password);
        editor.putString(KEY_REFER_CODE, referCode);
        editor.putString(KEY_LATITUDE, latitude);
        editor.putString(KEY_LONGITUDE, longitude);
        editor.commit();
    }


    public boolean checkLogin() {
        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return true;
        }
        return false;
    }


    public void logoutUser(Activity activity) {

        editor.clear();
        editor.commit();

        Intent i = new Intent(activity, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();

    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

}
