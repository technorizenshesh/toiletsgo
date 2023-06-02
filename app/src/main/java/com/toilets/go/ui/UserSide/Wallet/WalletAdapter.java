package com.toilets.go.ui.UserSide.Wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toilets.go.R;

import java.util.List;


public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<WalletResponse.Result> modelList;
    public WalletAdapter(Context context, List<WalletResponse.Result>
            modelList) {
        this.mContext = context;
        this.modelList = modelList;

    }


    @NonNull
    @Override
    public WalletAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_wallet_history, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof WalletAdapter.ViewHolder) {
            final WalletResponse.Result model = modelList.get(position);
            TextView tvName = holder.itemView.findViewById(R.id.tvName);
            TextView tvDate = holder.itemView.findViewById(R.id.tvDate);
            tvName.setText("+ $ "+model.amount+"         ");
            tvDate.setText("+ $ "+model.userName+"         ");
            tvName.setTextColor(mContext.getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
