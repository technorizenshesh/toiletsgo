package com.toilets.go.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.toilets.go.R;
import com.toilets.go.models.SuccessResGetCountry;
import com.toilets.go.models.SuccessResStabRes;

import java.util.List;


public class EstAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<SuccessResStabRes.Result> modelList;
    public EstAdapter(Context context, List<SuccessResStabRes.Result>
            modelList) {
        this.mContext = context;
        this.modelList = modelList;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_filter_price, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof ViewHolder) {
            final SuccessResStabRes.Result model = modelList.get(position);
            TextView tvName = holder.itemView.findViewById(R.id.tvName);
            tvName.setText(model.getName());
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
