package com.toilets.go.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.toilets.go.BR;
import com.toilets.go.R;
import com.toilets.go.databinding.ItemRowBinding;
import com.toilets.go.databinding.ItemRowUserBinding;
import com.toilets.go.models.SuccessResRequests;
import com.toilets.go.utills.CustomClickListener;

import java.util.List;

public class UserBookingAdapter extends
        RecyclerView.Adapter<UserBookingAdapter.ViewHolder> {

    private List<SuccessResRequests.Result> dataModelList;
    private Context context;
    CustomClickListener customClickListener;

    public UserBookingAdapter(List<SuccessResRequests.Result> dataModelList,
                              Context ctx, CustomClickListener customClickListener) {
        this.dataModelList = dataModelList;
        this.customClickListener = customClickListener;
        context = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        ItemRowUserBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_row_user, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SuccessResRequests.Result dataModel = dataModelList.get(position);
        holder.bind(dataModel);
         if (dataModel.getStatus().equalsIgnoreCase("Pending")){
             holder.itemRowBinding.tvstatus.setTextColor(context.getColor(R.color.red));
             holder.itemRowBinding.giveReview.setVisibility(View.GONE);

         }else {
             holder.itemRowBinding.giveReview.setVisibility(View.VISIBLE);
         }
          holder.itemRowBinding.giveReview.setOnClickListener( v -> {
              customClickListener.cardClicked(dataModel,"",position);
          });
         holder.itemRowBinding.viewDtl.setOnClickListener( v -> {
              customClickListener.cardClicked(dataModel,"viewDetails",position);
          });

    }


    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemRowUserBinding itemRowBinding;

        public ViewHolder(ItemRowUserBinding itemRowBinding) {
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