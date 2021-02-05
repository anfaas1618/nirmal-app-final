package com.mibtech.nirmalBakery.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.mibtech.nirmalBakery.R;

import com.mibtech.nirmalBakery.activity.ProductListActivity;
import com.mibtech.nirmalBakery.model.Category;


public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionHolder> {

    public ArrayList<Category> sectionList;

    public Activity activity;

    public SectionAdapter(Activity activity, ArrayList<Category> sectionList) {
        this.activity = activity;
        this.sectionList = sectionList;
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    @Override
    public void onBindViewHolder(SectionHolder holder1, final int position) {
        final Category section;
        section = sectionList.get(position);
        SectionHolder holder = holder1;
        holder.tvTitle.setText(section.getName());
        holder.tvSubTitle.setText(section.getSubtitle());

        switch (section.getStyle()) {
            case "style_1":
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                AdapterStyle1 adapter = new AdapterStyle1(activity, section.getProductList(), R.layout.offer_layout);
                holder.recyclerView.setAdapter(adapter);
                break;
            case "style_2":
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                AdapterStyle2 adapterStyle2 = new AdapterStyle2(activity, section.getProductList());
                holder.recyclerView.setAdapter(adapterStyle2);
                break;
            case "style_3":
                holder.recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                AdapterStyle1 adapter3 = new AdapterStyle1(activity, section.getProductList(), R.layout.layout_style_3);
                holder.recyclerView.setAdapter(adapter3);
                break;
        }

        holder.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, ProductListActivity.class);
                intent.putExtra("from", "section");
                intent.putExtra("name", section.getName());
                intent.putExtra("position", position);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);

            }
        });
    }

    @Override
    public SectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_layout, parent, false);
        return new SectionHolder(view);
    }

    public class SectionHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubTitle, tvMore;
        RecyclerView recyclerView;
        RelativeLayout relativeLayout;

        public SectionHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            tvMore = itemView.findViewById(R.id.tvMore);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
