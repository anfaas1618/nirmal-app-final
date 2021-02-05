package com.mibtech.nirmalBakery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.multidex.BuildConfig;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.mibtech.nirmalBakery.R;
import com.mibtech.nirmalBakery.adapter.SliderAdapter;
import com.mibtech.nirmalBakery.helper.ApiConfig;
import com.mibtech.nirmalBakery.helper.Constant;
import com.mibtech.nirmalBakery.helper.DatabaseHelper;
import com.mibtech.nirmalBakery.helper.VolleyCallback;
import com.mibtech.nirmalBakery.model.PriceVariation;
import com.mibtech.nirmalBakery.model.Product;
import com.mibtech.nirmalBakery.model.Slider;

public class ProductDetailActivity extends AppCompatActivity {

    int vpos;
    String from;
    Product product;
    PriceVariation priceVariation;
    ArrayList<PriceVariation> priceVariationslist;
    Toolbar toolbar;
    ImageView imgIndicator;
    public TextView txtProductName, txtqty, txtPrice, txtOriginalPrice, txtDiscountedPrice, txtMeasurement, imgarrow;
    public static ArrayList<Slider> sliderArrayList;
    public WebView webDescription;
    public TextView btncart;
    public ImageButton imgAdd, imgMinus;
    SpannableString spannableString;
    public int size;
    public ViewPager viewPager;
    public AppCompatSpinner spinner;
    TextView txtstatus;
    public ImageView imgFav;
    LinearLayout mMarkersLayout, lytqty;
    RelativeLayout lytmainprice;

    FrameLayout fragment_container;

    public static FragmentManager fragmentManager;
    DatabaseHelper databaseHelper;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        vpos = getIntent().getIntExtra("vpos", 0);
        from = getIntent().getStringExtra("from");

        InitializeViews();

        if (from != null && from.equals("share")) {
            GetProductDetail(getIntent().getStringExtra("id"));

        } else {

            product = (Product) getIntent().getSerializableExtra("model");
            priceVariationslist = product.getPriceVariations();
            SetProductDetails();


        }


    }

    private void GetProductDetail(String productid) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.PRODUCT_ID, productid);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                        //  System.out.println("======product " + response);
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            product = ApiConfig.GetProductList(jsonArray).get(0);
                            priceVariationslist = product.getPriceVariations();
                            product.setGlobalStock(Double.parseDouble(priceVariationslist.get(0).getStock()));
                            SetProductDetails();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ProductDetailActivity.this, Constant.GET_PRODUCT_DETAIL_URL, params, false);
    }


    private void InitializeViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        databaseHelper = new DatabaseHelper(ProductDetailActivity.this);
        lytqty = findViewById(R.id.lytqty);
        mMarkersLayout = findViewById(R.id.layout_markers);
        fragment_container = findViewById(R.id.fragment_container);

        viewPager = findViewById(R.id.viewPager);
        btncart = findViewById(R.id.btncart);
        txtProductName = findViewById(R.id.txtproductname);
        txtOriginalPrice = findViewById(R.id.txtoriginalprice);
        txtDiscountedPrice = findViewById(R.id.txtdiscountPrice);
        webDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtprice);
        txtMeasurement = findViewById(R.id.txtmeasurement);
        imgarrow = findViewById(R.id.imgarrow);
        imgFav = findViewById(R.id.imgFav);
        lytmainprice = findViewById(R.id.lytmainprice);
        txtqty = findViewById(R.id.txtqty);
        txtstatus = findViewById(R.id.txtstatus);
        imgAdd = findViewById(R.id.btnaddqty);
        imgMinus = findViewById(R.id.btnminusqty);
        spinner = findViewById(R.id.spinner);
        imgIndicator = findViewById(R.id.imgIndicator);
        imgarrow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dropdown, 0);
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


    private void SetProductDetails() {

        try {
            sliderArrayList = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(product.getOther_images());
            size = jsonArray.length();

            sliderArrayList.add(new Slider(product.getImage()));

            for (int i = 0; i < jsonArray.length(); i++) {
                sliderArrayList.add(new Slider(jsonArray.getString(i)));
            }

            viewPager.setAdapter(new SliderAdapter(sliderArrayList, ProductDetailActivity.this, R.layout.lyt_detail_slider, "detail"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, ProductDetailActivity.this);


            if (priceVariationslist.size() == 1) {
                spinner.setVisibility(View.GONE);
                lytmainprice.setEnabled(false);
                imgarrow.setVisibility(View.GONE);
                priceVariation = priceVariationslist.get(0);
                SetSelectedData(priceVariation);
            }

            if (!product.getIndicator().equals("0")) {
                imgIndicator.setVisibility(View.VISIBLE);
                if (product.getIndicator().equals("1"))
                    imgIndicator.setImageResource(R.drawable.veg_icon);
                else if (product.getIndicator().equals("2"))
                    imgIndicator.setImageResource(R.drawable.non_veg_icon);
            }
            CustomAdapter customAdapter = new CustomAdapter();
            spinner.setAdapter(customAdapter);


            webDescription.setVerticalScrollBarEnabled(true);
            webDescription.loadDataWithBaseURL("", product.getDescription(), "text/html", "UTF-8", "");
            webDescription.setBackgroundColor(getResources().getColor(R.color.white));
            txtProductName.setText(product.getName());

            spinner.setSelection(vpos);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int position) {
                    ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, ProductDetailActivity.this);
                }

                @Override
                public void onPageScrollStateChanged(int i) {
                }
            });

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    priceVariation = product.getPriceVariations().get(i);
                    SetSelectedData(priceVariation);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


            ApiConfig.SetFavOnImg(databaseHelper, imgFav, product.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void OnBtnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.lytshare:
                //ShareProduct();
                new ShareProduct().execute();
                break;
            case R.id.lytsave:
                ApiConfig.AddRemoveFav(databaseHelper, imgFav, product.getId());
                break;
            case R.id.btncart:
                OpenCart();
                break;
            case R.id.lytsimilarproducts:
                ViewMoreProduct();
                break;
            case R.id.lytmainprice:
                spinner.performClick();
                break;
            case R.id.btnminusqty:

                txtqty.setText(databaseHelper.AddUpdateOrder(priceVariation.getId(), product.getId(), false, ProductDetailActivity.this, false, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + priceVariation.getProductPrice()).split("=")[0]);
                invalidateOptionsMenu();
                break;
            case R.id.btnaddqty:
                if (priceVariation.getType().equals("loose")) {
                    String measurement = priceVariation.getMeasurement_unit_name();

                    if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                        double totalKg;
                        if (measurement.equals("kg") || measurement.equals("ltr"))
                            totalKg = (Integer.parseInt(priceVariation.getMeasurement()) * 1000);
                        else
                            totalKg = (Integer.parseInt(priceVariation.getMeasurement()));
                        double cartKg = ((databaseHelper.getTotalKG(product.getId()) + totalKg) / 1000);

                        if (cartKg <= product.getGlobalStock()) {
                            txtqty.setText(databaseHelper.AddUpdateOrder(priceVariation.getId(), product.getId(), true, ProductDetailActivity.this, false, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + priceVariation.getProductPrice()).split("=")[0]);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        RegularCartAdd();
                    }


                } else {
                    RegularCartAdd();
                }
                invalidateOptionsMenu();
                break;
        }

    }

    public void RegularCartAdd() {
        if (Double.parseDouble(databaseHelper.CheckOrderExists(priceVariation.getId(), product.getId())) < Double.parseDouble(priceVariation.getStock()))
            txtqty.setText(databaseHelper.AddUpdateOrder(priceVariation.getId(), product.getId(), true, ProductDetailActivity.this, false, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + priceVariation.getProductPrice()).split("=")[0]);
        else
            Toast.makeText(ProductDetailActivity.this, getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }

    private class ShareProduct extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Bitmap bitmap = null;
                URL url = null;
                url = new URL(product.getImage());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
                Date now = new Date();
                File file = new File(getExternalCacheDir(), formatter.format(now) + ".png");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

                String message = product.getName() + "\n";
                message = message + Constant.share_url + "itemdetail/" + product.getId() + "/" + product.getSlug();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.setDataAndType(uri, getContentResolver().getType(uri));
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

    }


    private void ViewMoreProduct() {
        Intent intent = new Intent(ProductDetailActivity.this, ProductListActivity.class);
        intent.putExtra("from", "regular");
        intent.putExtra("id", product.getSubcategory_id());
        intent.putExtra("name", product.getName());
        startActivity(intent);
    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return product.getPriceVariations().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
            TextView price = view.findViewById(R.id.txtprice);
            TextView txttitle = view.findViewById(R.id.txttitle);

            PriceVariation extra = product.getPriceVariations().get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
            price.setText(Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());

            if (i == 0) {
                txttitle.setVisibility(View.VISIBLE);
            } else {
                txttitle.setVisibility(View.GONE);
            }

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(getResources().getColor(R.color.red));
                price.setTextColor(getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(getResources().getColor(R.color.black));
                price.setTextColor(getResources().getColor(R.color.black));
            }

            return view;
        }
    }


    public void SetSelectedData(PriceVariation priceVariation) {

        txtMeasurement.setText(" ( " + priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + " ) ");
        txtPrice.setText(getString(R.string.offer_price) + Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getProductPrice());
        txtstatus.setText(priceVariation.getServe_for());


        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
            txtOriginalPrice.setText("");
            txtDiscountedPrice.setText("");
        } else {
            spannableString = new SpannableString(getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtOriginalPrice.setText(spannableString);
            double diff = Double.parseDouble(priceVariation.getPrice()) - Double.parseDouble(priceVariation.getProductPrice());
            txtDiscountedPrice.setText(getString(R.string.you_save) + Constant.SETTING_CURRENCY_SYMBOL + diff + priceVariation.getDiscountpercent());
        }

        if (priceVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            txtstatus.setVisibility(View.VISIBLE);
            lytqty.setVisibility(View.GONE);
        } else {
            txtstatus.setVisibility(View.INVISIBLE);
            lytqty.setVisibility(View.VISIBLE);
        }

        txtqty.setText(databaseHelper.CheckOrderExists(priceVariation.getId(), product.getId()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart, ProductDetailActivity.this));


        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_cart:
                OpenCart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void OpenCart() {
        startActivity(new Intent(getApplicationContext(), CartActivity.class));
    }


}
