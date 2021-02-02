package com.mibtech.optical.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.mibtech.optical.R;

import com.mibtech.optical.helper.ApiConfig;
import com.mibtech.optical.helper.AppController;
import com.mibtech.optical.helper.Constant;
import com.mibtech.optical.helper.Session;
import com.mibtech.optical.helper.VolleyCallback;
import com.mibtech.optical.model.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView txtdob, tvCurrent;
    EditText edtname, edtemail, edtaddress, edtPinCode, edtMobile;//edtcity;
    Session session;
    Toolbar toolbar;
    String city = "0", area = "0", cityId, areaId;
    ArrayList<City> cityArrayList, areaList;
    AppCompatSpinner cityspinner, areaSpinner;
    SupportMapFragment mapFragment;
    double latitude = 0.0, longitude = 0.0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        cityArrayList = new ArrayList<>();
        areaList = new ArrayList<>();
        cityspinner = findViewById(R.id.cityspinner);
        areaSpinner = findViewById(R.id.areaSpinner);
        toolbar = findViewById(R.id.toolbar);
        txtdob = findViewById(R.id.txtdob);
        tvCurrent = findViewById(R.id.tvCurrent);
        edtname = findViewById(R.id.edtname);
        edtemail = findViewById(R.id.edtemail);
        edtMobile = findViewById(R.id.edtMobile);
        edtaddress = findViewById(R.id.edtaddress);
        edtPinCode = findViewById(R.id.edtPinCode);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(ProfileActivity.this);

        final Calendar c = Calendar.getInstance();

        txtdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ProfileActivity.this, AlertDialog.THEME_HOLO_LIGHT, pickerListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        edtname.setText(session.getData(Session.KEY_NAME));
        edtemail.setText(session.getData(Session.KEY_EMAIL));
        edtaddress.setText(session.getData(Session.KEY_ADDRESS));
        txtdob.setText(session.getData(Session.KEY_DOB));
        /*edtcity.setText(session.getData(Session.KEY_CITY));*/
        edtPinCode.setText(session.getData(Session.KEY_PINCODE));
        edtMobile.setText(session.getData(Session.KEY_MOBILE));
        cityId = session.getData(Session.KEY_CITY_ID);
        areaId = session.getData(Session.KEY_AREA_ID);

        SetSpinnerData();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void SetSpinnerData() {
        Map<String, String> params = new HashMap<String, String>();
        cityArrayList.clear();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);

                        int pos = 0;
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            cityArrayList.add(0, new City("0", getString(R.string.select_city)));
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                cityArrayList.add(new City(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME)));
                                if (jsonObject.getString(Constant.ID).equals(cityId))
                                    pos = i;

                            }
                            cityspinner.setAdapter(new ArrayAdapter<>(ProfileActivity.this, R.layout.spinner_item, cityArrayList));
                            cityspinner.setSelection(pos + 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ProfileActivity.this, Constant.CITY_URL, params, false);

        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityId = cityArrayList.get(position).getCity_id();
                city = cityspinner.getSelectedItem().toString();
                SetAreaSpinnerData(cityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void SetAreaSpinnerData(String cityId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.CITY_ID, cityId);
        areaList.clear();
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {

                        JSONObject objectbject = new JSONObject(response);
                        int pos = 0;
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            areaList.add(0, new City("0", getString(R.string.select_area)));
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                areaList.add(new City(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME)));
                                if (jsonObject.getString(Constant.ID).equals(areaId))
                                    pos = i;

                            }
                            areaSpinner.setAdapter(new ArrayAdapter<City>(ProfileActivity.this, R.layout.spinner_item, areaList));
                            areaSpinner.setSelection(pos + 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ProfileActivity.this, Constant.GET_AREA_BY_CITY, params, false);

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaId = areaList.get(position).getCity_id();
                area = areaSpinner.getSelectedItem().toString();
                //incode = cityArrayList.get(position).getPincode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String monthString = String.valueOf(selectedMonth + 1);
            String dateString = String.valueOf(selectedDay);
            if (monthString.length() == 1) {
                monthString = "0" + monthString;
            }
            if (dateString.length() == 1) {
                dateString = "0" + dateString;
            }
            txtdob.setText(dateString + "-" + monthString + "-" + selectedYear);
        }
    };

    public void OnClick(View view) {
        int id = view.getId();
        if (id == R.id.btnsubmit) {
            final String name = edtname.getText().toString();
            final String email = edtemail.getText().toString();
            final String address = edtaddress.getText().toString();
            final String dob = txtdob.getText().toString();
            final String mobile = edtMobile.getText().toString();
            /*final String city = edtcity.getText().toString();*/
            final String pincode = edtPinCode.getText().toString();

            if (ApiConfig.CheckValidattion(name, false, false))
                edtname.setError(getString(R.string.enter_name));
            if (ApiConfig.CheckValidattion(email, false, false))
                edtemail.setError(getString(R.string.enter_email));
            else if (ApiConfig.CheckValidattion(email, true, false))
                edtemail.setError(getString(R.string.enter_valid_email));
            else if (cityId.equals("0"))
                Toast.makeText(ProfileActivity.this, getResources().getString(R.string.selectcity), Toast.LENGTH_LONG).show();
            else if (areaId.equals("0"))
                Toast.makeText(ProfileActivity.this, getResources().getString(R.string.selectarea), Toast.LENGTH_LONG).show();
            else if (ApiConfig.CheckValidattion(address, false, false))
                edtaddress.setError(getString(R.string.enter_address));
            else if (ApiConfig.CheckValidattion(pincode, false, false))
                edtPinCode.setError(getString(R.string.enter_pincode));
            else if (AppController.isConnected(ProfileActivity.this)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.TYPE, Constant.EDIT_PROFILE);
                params.put(Constant.ID, session.getData(Session.KEY_ID));
                params.put(Constant.NAME, name);
                params.put(Constant.EMAIL, email);
                params.put(Constant.CITY_ID, cityId);
                params.put(Constant.AREA_ID, areaId);
                params.put(Constant.MOBILE, mobile);
                params.put(Constant.STREET, address);
                params.put(Constant.PINCODE, pincode);
                params.put(Constant.DOB, dob);
                params.put(Constant.LONGITUDE, session.getCoordinates(Session.KEY_LONGITUDE));
                params.put(Constant.LATITUDE, session.getCoordinates(Session.KEY_LATITUDE));
                params.put(Constant.FCM_ID, AppController.getInstance().getDeviceToken());
                //  System.out.println("====update res " + params.toString());
                ApiConfig.RequestToVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        System.out.println("=================* " + response);
                        if (result) {
                            try {
                                JSONObject objectbject = new JSONObject(response);
                                if (!objectbject.getBoolean(Constant.ERROR)) {
                                    session.setData(Session.KEY_NAME, name);
                                    session.setData(Session.KEY_EMAIL, email);
                                    session.setData(Session.KEY_CITY, city);
                                    session.setData(Session.KEY_AREA, area);
                                    session.setData(Session.KEY_MOBILE, mobile);
                                    session.setData(Session.KEY_CITY_ID, cityId);
                                    session.setData(Session.KEY_AREA_ID, areaId);
                                    session.setData(Session.KEY_ADDRESS, address);
                                    session.setData(Session.KEY_PINCODE, pincode);
                                    session.setData(Session.KEY_DOB, dob);
                                    DrawerActivity.tvName.setText(name);
                                }
                                Toast.makeText(ProfileActivity.this, objectbject.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, ProfileActivity.this, Constant.RegisterUrl, params, true);
            }


        } else if (id == R.id.imglogout) {
            session.logoutUser(ProfileActivity.this);
        } else if (id == R.id.txtchangepassword) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class).putExtra("from", "changepsw"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;
        double saveLatitude = Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE));
        double saveLongitude = Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE));
        mMap.clear();

        LatLng latLng = new LatLng(saveLatitude, saveLongitude);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(getString(R.string.current_location)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        latitude = Double.parseDouble(session.getCoordinates(Session.KEY_LATITUDE));
        longitude = Double.parseDouble(session.getCoordinates(Session.KEY_LONGITUDE));
        tvCurrent.setText(getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, ProfileActivity.this));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mapFragment.getMapAsync(ProfileActivity.this);
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void UpdateLocation(View view) {
        if (ApiConfig.isGPSEnable(ProfileActivity.this))
            startActivity(new Intent(ProfileActivity.this, MapActivity.class));
        else
            ApiConfig.displayLocationSettingsRequest(ProfileActivity.this);
    }
}
