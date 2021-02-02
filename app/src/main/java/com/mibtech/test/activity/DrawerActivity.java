package com.mibtech.test.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.mibtech.test.R;
import com.mibtech.test.helper.ApiConfig;
import com.mibtech.test.helper.Constant;
import com.mibtech.test.helper.Session;
import com.mibtech.test.helper.VolleyCallback;

public class DrawerActivity extends AppCompatActivity {
    public NavigationView navigationView;
    public DrawerLayout drawer;
    public ActionBarDrawerToggle drawerToggle;
    protected FrameLayout frameLayout;
    public TextView tvMobile;
    public static TextView tvName, tvWallet;
    Session session;
    LinearLayout lytProfile;
    LinearLayout lytWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ApiConfig.transparentStatusAndNavigation(DrawerActivity.this);
        setContentView(R.layout.activity_drawer);

        frameLayout = findViewById(R.id.content_frame);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        View header = navigationView.getHeaderView(0);
        lytWallet = header.findViewById(R.id.lytWallet);
        tvWallet = header.findViewById(R.id.tvWallet);
        tvName = header.findViewById(R.id.header_name);
        tvMobile = header.findViewById(R.id.tvMobile);
        lytProfile = header.findViewById(R.id.lytProfile);
        session = new Session(DrawerActivity.this);

        if (session.isUserLoggedIn()) {
            tvName.setText(session.getData(Session.KEY_NAME));
            tvMobile.setText(session.getData(Session.KEY_MOBILE));
            lytWallet.setVisibility(View.VISIBLE);
            tvWallet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet_white, 0, 0, 0);
            DrawerActivity.tvWallet.setText(getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
            ApiConfig.getWalletBalance(DrawerActivity.this, session);
            // tvWallet.setText(getString(R.string.wallet_balance)+"\t:\t"+ApiConfig.getWalletBalance(DrawerActivity.this, session));;


        } else {
            lytWallet.setVisibility(View.GONE);
            tvName.setText(getResources().getString(R.string.is_login));
        }
        lytProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (session.isUserLoggedIn())
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                else
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        setupNavigationDrawer();

    }

    public void getWalletBalance(Activity activity, Session session) {

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

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.USER_DATA_URL, params, false);

    }

    private void setupNavigationDrawer() {
        Menu nav_Menu = navigationView.getMenu();
        if (session.isUserLoggedIn()) {
            nav_Menu.findItem(R.id.menu_profile).setVisible(true);
            nav_Menu.findItem(R.id.menu_logout).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.menu_logout).setVisible(false);
            nav_Menu.findItem(R.id.menu_profile).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawer.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.notifications:
                        startActivity(new Intent(getApplicationContext(), NotificationList.class));
                        break;
                    case R.id.faq:
                        Intent faq = new Intent(getApplicationContext(), WebViewActivity.class);
                        faq.putExtra("type", "faq");
                        startActivity(faq);
                        break;
                    case R.id.menu_terms:
                        Intent terms = new Intent(getApplicationContext(), WebViewActivity.class);
                        terms.putExtra("type", "terms");
                        startActivity(terms);
                        // ApiConfig.OpenBottomDialog("terms", getApplicationContext());
                        break;
                    case R.id.contact:
                        Intent contact = new Intent(getApplicationContext(), WebViewActivity.class);
                        contact.putExtra("type", "contact");
                        startActivity(contact);
                        break;
                    case R.id.about_us:
                        Intent about = new Intent(getApplicationContext(), WebViewActivity.class);
                        about.putExtra("type", "about");
                        startActivity(about);
                        break;
                    case R.id.menu_privacy:
                        Intent privacy = new Intent(getApplicationContext(), WebViewActivity.class);
                        privacy.putExtra("type", "privacy");
                        startActivity(privacy);

                        break;
                    case R.id.menu_home:
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        break;
                    case R.id.menu_profile:
                        if (session.isUserLoggedIn())
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        else
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        break;
                    case R.id.refer:
                        if (session.isUserLoggedIn())
                            startActivity(new Intent(getApplicationContext(), ReferEarnActivity.class));
                        else
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        break;
                    case R.id.cart:
                        startActivity(new Intent(getApplicationContext(), CartActivity.class));
                        break;

                    case R.id.changePass:
                        Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                        if (session.isUserLoggedIn())
                            intent1.putExtra("from", "changepsw");
                        startActivity(intent1);
                        break;
                    case R.id.menu_tracker:
                        if (session.isUserLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), OrderListActivity.class));
                        } else
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        break;
                    case R.id.menu_share:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.take_a_look) + "\"" + getString(R.string.app_name) + "\" - " + Constant.PLAY_STORE_LINK + getPackageName());
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
                        break;
                    case R.id.menu_rate:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_LINK + getPackageName())));
                        break;
                    case R.id.menu_logout:
                        session.logoutUser(DrawerActivity.this);
                        break;
                }

                return true;
            }
        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
}
