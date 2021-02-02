package com.mibtech.test.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import com.mibtech.test.R;

public class ProductHolder extends RecyclerView.ViewHolder {
    TextView productName, productPrice, txtqty, Measurement, showDiscount, originalPrice, txtstatus, imgarrow;
    NetworkImageView imgThumb;
    ImageView imgFav, imgIndicator;
    CardView lytmain;
    AppCompatSpinner spinner;
    public ImageButton imgAdd, imgMinus;
    LinearLayout qtyLyt;

    public ProductHolder(View itemView) {
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