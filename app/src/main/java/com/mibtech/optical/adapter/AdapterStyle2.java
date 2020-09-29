package com.mibtech.optical.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import com.mibtech.optical.R;
import com.mibtech.optical.activity.ProductDetailActivity;
import com.mibtech.optical.helper.AppController;
import com.mibtech.optical.model.Product;


/**
 * Created by shree1 on 3/16/2017.
 */

public class AdapterStyle2 extends RecyclerView.Adapter<AdapterStyle2.VideoHolder> {

    public ArrayList<Product> productList;

    public Activity activity;
    public
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public AdapterStyle2(Activity activity, ArrayList<Product> productList) {
        this.activity = activity;
        this.productList = productList;

    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public NetworkImageView imgStyle2_1, imgStyle2_2, imgStyle2_3;
        public TextView tvStyle2_1, tvStyle2_2, tvStyle2_3, tvSubStyle2_1, tvSubStyle2_2, tvSubStyle2_3;
        public RelativeLayout layoutStyle2_1, layoutStyle2_2, layoutStyle2_3;

        public VideoHolder(View itemView) {
            super(itemView);
            imgStyle2_1 = itemView.findViewById(R.id.imgStyle2_1);
            imgStyle2_2 = itemView.findViewById(R.id.imgStyle2_2);
            imgStyle2_3 = itemView.findViewById(R.id.imgStyle2_3);
            tvStyle2_1 = itemView.findViewById(R.id.tvStyle2_1);
            tvStyle2_2 = itemView.findViewById(R.id.tvStyle2_2);
            tvStyle2_3 = itemView.findViewById(R.id.tvStyle2_3);
            tvSubStyle2_1 = itemView.findViewById(R.id.tvSubStyle2_1);
            tvSubStyle2_2 = itemView.findViewById(R.id.tvSubStyle2_2);
            tvSubStyle2_3 = itemView.findViewById(R.id.tvSubStyle2_3);
            layoutStyle2_1 = itemView.findViewById(R.id.layoutStyle2_1);
            layoutStyle2_2 = itemView.findViewById(R.id.layoutStyle2_2);
            layoutStyle2_3 = itemView.findViewById(R.id.layoutStyle2_3);
        }


    }


    @Override
    public int getItemCount() {
        return 1;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(VideoHolder holder, final int position) {

        if (productList.size() > 0) {
            holder.tvStyle2_1.setText(productList.get(0).getName());
            holder.imgStyle2_1.setImageUrl(productList.get(0).getImage(), imageLoader);
            productList.get(0).setGlobalStock(Double.parseDouble(productList.get(0).getPriceVariations().get(0).getStock()));
        }
        if (productList.size() > 1) {
            holder.tvStyle2_2.setText(productList.get(1).getName());
            holder.imgStyle2_2.setImageUrl(productList.get(1).getImage(), imageLoader);
            productList.get(1).setGlobalStock(Double.parseDouble(productList.get(1).getPriceVariations().get(0).getStock()));
        }


        if (productList.size() > 2) {
            holder.tvStyle2_3.setText(productList.get(2).getName());
            holder.imgStyle2_3.setImageUrl(productList.get(2).getImage(), imageLoader);
            productList.get(2).setGlobalStock(Double.parseDouble(productList.get(2).getPriceVariations().get(0).getStock()));
        }


        holder.layoutStyle2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", 0).putExtra("model", productList.get(0)));
            }
        });
        holder.layoutStyle2_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", 0).putExtra("model", productList.get(1)));
            }
        });
        holder.layoutStyle2_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos", 0).putExtra("model", productList.get(2)));
            }
        });

    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_style_2, parent, false);
        return new VideoHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return getItemId(position);
    }
}
