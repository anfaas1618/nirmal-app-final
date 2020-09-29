package com.mibtech.optical.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.mibtech.optical.R;
import com.mibtech.optical.activity.ProductDetailActivity;
import com.mibtech.optical.helper.ApiConfig;
import com.mibtech.optical.helper.Constant;
import com.mibtech.optical.helper.DatabaseHelper;
import com.mibtech.optical.model.PriceVariation;
import com.mibtech.optical.model.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {
    public ArrayList<Product> productList;
    public Activity activity;
    public int resource;
    SpannableString spannableString;
    DatabaseHelper databaseHelper;

    public ProductAdapter(ArrayList<Product> productList, int resource, Activity activity) {
        this.productList = productList;
        this.resource = resource;
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, null);
        ProductHolder productHolder = new ProductHolder(v);
        return productHolder;
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, final int position) {
        final Product product = productList.get(position);
        holder.setIsRecyclable(false);
        final ArrayList<PriceVariation> priceVariations = product.getPriceVariations();
        product.setGlobalStock(Double.parseDouble(priceVariations.get(0).getStock()));
        if (priceVariations.size() == 1) {
            holder.imgarrow.setVisibility(View.GONE);
        }

        if (!product.getIndicator().equals("0")) {
            holder.imgIndicator.setVisibility(View.VISIBLE);
            if (product.getIndicator().equals("1"))
                holder.imgIndicator.setImageResource(R.drawable.veg_icon);
            else if (product.getIndicator().equals("2"))
                holder.imgIndicator.setImageResource(R.drawable.non_veg_icon);
        }
        holder.productName.setText(Html.fromHtml("<font color='#000000'><b>" + product.getName() + "</b></font> - <small>" + product.getDescription().replaceFirst("<p>", "").replaceFirst("</p>", "") + "</small>"));
        holder.imgThumb.setImageUrl(product.getImage(), Constant.imageLoader);

        CustomAdapter customAdapter = new CustomAdapter(activity, priceVariations, holder, product);
        holder.spinner.setAdapter(customAdapter);

        holder.imgarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.spinner.performClick();
            }
        });

        SetSelectedData(product, holder, priceVariations.get(0));

        holder.lytmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.SELECTEDPRODUCT_POS = position + "=" + product.getId();
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition()).putExtra("model", product));
            }
        });

        ApiConfig.SetFavOnImg(databaseHelper, holder.imgFav, product.getId());

        holder.imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiConfig.AddRemoveFav(databaseHelper, holder.imgFav, product.getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<PriceVariation> extraList;
        LayoutInflater inflter;
        ProductHolder holder;
        Product product;

        public CustomAdapter(Context applicationContext, ArrayList<PriceVariation> extraList, ProductHolder holder, Product product) {
            this.context = applicationContext;
            this.extraList = extraList;
            this.holder = holder;
            this.product = product;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return extraList.size();
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
            view = inflter.inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
            TextView price = view.findViewById(R.id.txtprice);
            TextView txttitle = view.findViewById(R.id.txttitle);

            PriceVariation extra = extraList.get(i);
            measurement.setText(extra.getMeasurement() + " " + extra.getMeasurement_unit_name());
            price.setText(Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());

            if (i == 0) {
                txttitle.setVisibility(View.VISIBLE);
            } else {
                txttitle.setVisibility(View.GONE);
            }

            if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                measurement.setTextColor(context.getResources().getColor(R.color.red));
                price.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(context.getResources().getColor(R.color.black));
                price.setTextColor(context.getResources().getColor(R.color.black));
            }

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    PriceVariation extra = extraList.get(i);
                    SetSelectedData(product, holder, extra);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

    public void SetSelectedData(final Product product, final ProductHolder holder, final PriceVariation extra) {

        holder.Measurement.setText(extra.getMeasurement() + extra.getMeasurement_unit_name());
        holder.productPrice.setText(activity.getResources().getString(R.string.offer_price) + Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());
        holder.txtstatus.setText(extra.getServe_for());

        if (extra.getDiscounted_price().equals("0") || extra.getDiscounted_price().equals("")) {
            holder.originalPrice.setText("");
            holder.showDiscount.setText("");
        } else {
            spannableString = new SpannableString(activity.getResources().getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + extra.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.originalPrice.setText(spannableString);
            double diff = Double.parseDouble(extra.getPrice()) - Double.parseDouble(extra.getProductPrice());
            holder.showDiscount.setText(activity.getResources().getString(R.string.you_save) + Constant.SETTING_CURRENCY_SYMBOL + diff + extra.getDiscountpercent());

        }


        if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            holder.txtstatus.setVisibility(View.VISIBLE);
            holder.txtstatus.setTextColor(Color.RED);
            holder.qtyLyt.setVisibility(View.GONE);
        } else {
            holder.txtstatus.setVisibility(View.INVISIBLE);
            holder.qtyLyt.setVisibility(View.VISIBLE);
        }
        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (extra.getType().equals("loose")) {
                    String measurement = extra.getMeasurement_unit_name();

                    if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                        double totalKg;
                        if (measurement.equals("kg") || measurement.equals("ltr"))
                            totalKg = (Integer.parseInt(extra.getMeasurement()) * 1000);
                        else
                            totalKg = (Integer.parseInt(extra.getMeasurement()));
                        double cartKg = ((databaseHelper.getTotalKG(product.getId()) + totalKg) / 1000);

                        if (cartKg <= product.getGlobalStock()) {
                            holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), true, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        RegularCartAdd(product, holder, extra);
                    }


                } else {
                    RegularCartAdd(product, holder, extra);
                }

                activity.invalidateOptionsMenu();
            }
        });
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), false, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
                activity.invalidateOptionsMenu();
            }
        });
        holder.txtqty.setText(databaseHelper.CheckOrderExists(extra.getId(), product.getId()));

    }

    public void RegularCartAdd(final Product product, final ProductHolder holder, final PriceVariation extra) {

        if (Double.parseDouble(databaseHelper.CheckOrderExists(extra.getId(), product.getId())) < Double.parseDouble(extra.getStock()))
            holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), true, activity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
        else
            Toast.makeText(activity, activity.getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }
}

