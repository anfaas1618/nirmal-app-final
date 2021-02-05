package com.mibtech.nirmalBakery.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.mibtech.nirmalBakery.R;
import com.mibtech.nirmalBakery.adapter.NotificationAdapter;
import com.mibtech.nirmalBakery.helper.ApiConfig;
import com.mibtech.nirmalBakery.helper.Constant;
import com.mibtech.nirmalBakery.helper.VolleyCallback;
import com.mibtech.nirmalBakery.model.Notification;

public class NotificationList extends AppCompatActivity {


    RecyclerView recyclerView;
    ArrayList<Notification> notifications;
    ProgressBar progressbar;
    Toolbar toolbar;
    SwipeRefreshLayout swipeLayout;
    TextView tvAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.notifications));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeLayout = findViewById(R.id.swipeLayout);
        progressbar = findViewById(R.id.progressbar);
        tvAlert = findViewById(R.id.tvAlert);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationList.this));
        getNotificationData(NotificationList.this);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificationData(NotificationList.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

    }

    public void getNotificationData(final Activity activity) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_NOTIFICATIONS, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("===n response " + response);
                        notifications = new ArrayList<>();
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Notification notification = new Notification();
                            notification.setTitle(jsonObject.getString(Constant.NAME));
                            notification.setMessage(jsonObject.getString(Constant.SUBTITLE));
                            notification.setImage(jsonObject.getString(Constant.IMAGE));
                            notifications.add(notification);
                        }
                        NotificationAdapter notificationAdapter = new NotificationAdapter(NotificationList.this, notifications);
                        recyclerView.setAdapter(notificationAdapter);
                        progressbar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.GET_SECTION_URL, params, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}