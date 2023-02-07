package com.toilets.go.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.toilets.go.BR;
import com.toilets.go.R;
import com.toilets.go.databinding.ItemRowBinding;
import com.toilets.go.databinding.ItemRowNotificatinBinding;
import com.toilets.go.listeners.NotificationClickListener;
import com.toilets.go.models.SuccessResNotifications;
import com.toilets.go.utills.CustomClickListener;

import java.util.List;

public class NotificationListAdapter extends
        RecyclerView.Adapter<NotificationListAdapter.ViewHolder>
         {

    private List<SuccessResNotifications.Result> dataModelList;
    private Context context;
             NotificationClickListener customClickListener ;

    public NotificationListAdapter(List<SuccessResNotifications.Result> dataModelList,
                                   Context ctx, NotificationClickListener customClickListener ) {
        this.dataModelList = dataModelList;
        this.customClickListener = customClickListener;
        context = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        ItemRowNotificatinBinding binding= DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_row_notificatin, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SuccessResNotifications.Result dataModel = dataModelList.get(position);
        holder.bind(dataModel);


        holder.itemRowBinding.getRoot().setOnClickListener( v -> {
            customClickListener.notificationCardClicked(dataModel,"Reject",position);
        });

    }


    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemRowNotificatinBinding itemRowBinding;

        public ViewHolder(ItemRowNotificatinBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }

        public void bind(Object obj) {
            itemRowBinding.setVariable(BR.model, obj);
            itemRowBinding.executePendingBindings();
        }
    }

             public void removeAt(int position) {
                 dataModelList.remove(position);
                 notifyItemRemoved(position);
                 notifyItemRangeChanged(position, dataModelList.size());
             }
}