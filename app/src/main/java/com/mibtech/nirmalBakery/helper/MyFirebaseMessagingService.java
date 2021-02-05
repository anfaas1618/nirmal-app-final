package com.mibtech.nirmalBakery.helper;


import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.mibtech.nirmalBakery.activity.MainActivity;
import com.mibtech.nirmalBakery.activity.OrderListActivity;
import com.mibtech.nirmalBakery.activity.ProductDetailActivity;
import com.mibtech.nirmalBakery.activity.SubCategoryActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                System.out.println("=====n_response " + json.toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void sendPushNotification(JSONObject json) {
        try {

            JSONObject data = json.getJSONObject(Constant.DATA);

            String title = data.getString("title");
            String message = data.getString("message");
            String imageUrl = data.getString("image");

            String type = data.getString("type");
            String id = data.getString("id");

            Intent intent = null;


            if (type.equals("category")) {
                intent = new Intent(getApplicationContext(), SubCategoryActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("name", title);

            } else if (type.equals("product")) {
                intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("vpos", 0);
                intent.putExtra("from", "share");
            } else if (type.equals("order")) {
                intent = new Intent(getApplicationContext(), OrderListActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }

            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
            //Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            if (imageUrl.equals("null") || imageUrl.equals("")) {
                mNotificationManager.showSmallNotification(title, message, intent);
            } else {
                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        AppController.getInstance().setDeviceToken(s);
        //MainActivity.UpdateToken(s);
    }

}
