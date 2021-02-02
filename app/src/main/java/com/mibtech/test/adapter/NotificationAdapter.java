package com.mibtech.test.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import com.mibtech.test.R;
import com.mibtech.test.helper.Constant;
import com.mibtech.test.model.Notification;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationItemHolder> {
    Activity activity;
    ArrayList<Notification> notifications;

    public NotificationAdapter(Activity activity, ArrayList<Notification> notifications) {
        this.activity = activity;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_notification_list, null);
        NotificationItemHolder notificationItemHolder = new NotificationItemHolder(v);
        return notificationItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationItemHolder holder, int position) {

        Notification notification = notifications.get(position);
        holder.image.setImageUrl(notification.getImage(), Constant.imageLoader);
        if (!notification.getImage().isEmpty())
            holder.image.setVisibility(View.VISIBLE);
        else
            holder.image.setVisibility(View.GONE);

        holder.tvTitle.setText(Html.fromHtml(notification.getTitle()));
        holder.tvMessage.setText(Html.fromHtml(notification.getMessage()));

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationItemHolder extends RecyclerView.ViewHolder {

        NetworkImageView image;
        TextView tvTitle, tvMessage;


        public NotificationItemHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

}
