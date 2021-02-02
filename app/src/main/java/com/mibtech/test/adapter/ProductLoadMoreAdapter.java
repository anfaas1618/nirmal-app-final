package com.mibtech.optical.adapter;

import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import com.mibtech.optical.R;
import com.mibtech.optical.activity.ProductDetailActivity;
import com.mibtech.optical.helper.ApiConfig;
import com.mibtech.optical.helper.Constant;
import com.mibtech.optical.helper.DatabaseHelper;
import com.mibtech.optical.model.PriceVariation;
import com.mibtech.optical.model.Product;

import java.util.ArrayList;

public class ProductLoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Product> mDataset = new ArrayList<>();
    private Context mcontext;
    private Activity mactivity;

    DatabaseHelper databaseHelper;

    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    public boolean isLoading;

    public int resource;
    SpannableString spannableString;


    public void add(int position, Product item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }


    public ProductLoadMoreAdapter(Context context, ArrayList<Product> myDataset, RecyclerView recyclerView, int resource) {
        this.mcontext = context;
        this.mactivity = (Activity) context;
        this.mDataset = myDataset;
        this.resource = resource;
        databaseHelper = new DatabaseHelper(mcontext);
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mactivity).inflate(resource, parent, false);
            return new ViewHolderRow(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mactivity).inflate(R.layout.item_progressbar, parent, false);
            return new ViewHolderLoading(view);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderparent, final int position) {

        if (holderparent instanceof ViewHolderRow) {
            final ViewHolderRow holder = (ViewHolderRow) holderparent;
            holder.setIsRecyclable(false);
            final Product product = mDataset.get(position);

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
            holder.imgThumb.setDefaultImageResId(R.drawable.placeholder);
            holder.imgThumb.setErrorImageResId(R.drawable.placeholder);

            holder.imgThumb.setImageUrl(product.getImage(), Constant.imageLoader);

            CustomAdapter customAdapter = new CustomAdapter(mcontext, priceVariations, holder, product);
            holder.spinner.setAdapter(customAdapter);
            holder.imgarrow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dropdown, 0);
            holder.imgarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.spinner.performClick();
                }
            });

            holder.lytmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //System.out.println("=================== =*=" + holder.spinner.getSelectedItemPosition());

                    Constant.SELECTEDPRODUCT_POS = position + "=" + product.getId();
                    mcontext.startActivity(new Intent(mcontext, ProductDetailActivity.class).putExtra("vpos", priceVariations.size() == 1 ? 0 : holder.spinner.getSelectedItemPosition()).putExtra("model", product));
                }
            });

            ApiConfig.SetFavOnImg(databaseHelper, holder.imgFav, product.getId());

            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiConfig.AddRemoveFav(databaseHelper, holder.imgFav, product.getId());
                }
            });

            SetSelectedData(product, holder, priceVariations.get(0));

        } else if (holderparent instanceof ViewHolderLoading) {
            ViewHolderLoading loadingViewHolder = (ViewHolderLoading) holderparent;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        Product product = mDataset.get(position);
        if (product != null)
            return Integer.parseInt(product.getId());
        else
            return position;
    }

    public void setLoaded() {
        isLoading = false;
    }

    private class ViewHolderLoading extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ViewHolderLoading(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.itemProgressbar);
        }
    }


    public class ViewHolderRow extends RecyclerView.ViewHolder {
        TextView productName, productPrice, txtqty, Measurement, showDiscount, originalPrice, txtstatus;
        NetworkImageView imgThumb;
        ImageView imgFav, imgIndicator;
        TextView imgarrow;
        CardView lytmain;
        AppCompatSpinner spinner;
        public ImageButton imgAdd, imgMinus;
        LinearLayout qtyLyt;

        public ViewHolderRow(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.txtprice);
            showDiscount = itemView.findViewById(R.id.showDiscount);
            originalPrice = itemView.findViewById(R.id.txtoriginalprice);
            Measurement = itemView.findViewById(R.id.txtmeasurement);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            imgIndicator = itemView.findViewById(R.id.imgIndicator);
            imgarrow = itemView.findViewById(R.id.imgarrow);
            imgAdd = itemView.findViewById(R.id.btnaddqty);
            imgMinus = itemView.findViewById(R.id.btnminusqty);
            txtqty = itemView.findViewById(R.id.txtqty);
            qtyLyt = itemView.findViewById(R.id.qtyLyt);
            imgFav = itemView.findViewById(R.id.imgFav);
            lytmain = itemView.findViewById(R.id.lytmain);
            spinner = itemView.findViewById(R.id.spinner);

        }

    }

    public class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<PriceVariation> extraList;
        LayoutInflater inflter;
        ViewHolderRow holder;
        Product product;

        public CustomAdapter(Context applicationContext, ArrayList<PriceVariation> extraList, ViewHolderRow holder, Product product) {
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

        @SuppressLint("SetTextI18n")
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

                    PriceVariation priceVariation = extraList.get(i);
                    SetSelectedData(product, holder, priceVariation);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;
        }
    }

    @SuppressLint("SetTextI18n")
    public void SetSelectedData(final Product product, final ViewHolderRow holder, final PriceVariation extra) {

        holder.Measurement.setText(extra.getMeasurement() + extra.getMeasurement_unit_name());
        holder.productPrice.setText(mactivity.getResources().getString(R.string.offer_price) + Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());

        holder.txtstatus.setText(extra.getServe_for());

        if (extra.getDiscounted_price().equals("0") || extra.getDiscounted_price().equals("")) {
            holder.originalPrice.setText("");
            holder.showDiscount.setText("");
            holder.productPrice.setText(mactivity.getResources().getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + extra.getProductPrice());
        } else {
            spannableString = new SpannableString(mactivity.getResources().getString(R.string.mrp) + Constant.SETTING_CURRENCY_SYMBOL + extra.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.originalPrice.setText(spannableString);
            double diff = Double.parseDouble(extra.getPrice()) - Double.parseDouble(extra.getProductPrice());
            holder.showDiscount.setText(mactivity.getResources().getString(R.string.you_save) + Constant.SETTING_CURRENCY_SYMBOL + diff + extra.getDiscountpercent());
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
                            holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), true, mactivity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
                        } else {
                            Toast.makeText(mactivity, mactivity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        RegularCartAdd(product, holder, extra);
                    }
                } else {
                    RegularCartAdd(product, holder, extra);
                }

                mactivity.invalidateOptionsMenu();
            }
        });

        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), false, mactivity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
                mactivity.invalidateOptionsMenu();
            }
        });
        holder.txtqty.setText(databaseHelper.CheckOrderExists(extra.getId(), product.getId()));


    }

    public void RegularCartAdd(final Product product, final ViewHolderRow holder, final PriceVariation extra) {

        if (Double.parseDouble(databaseHelper.CheckOrderExists(extra.getId(), product.getId())) < Double.parseDouble(extra.getStock()))
            holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), true, mactivity, false, Double.parseDouble(extra.getProductPrice()), extra.getMeasurement() + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getProductPrice()).split("=")[0]);
        else
            Toast.makeText(mactivity, mactivity.getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }

}
