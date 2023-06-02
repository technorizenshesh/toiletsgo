package com.toilets.go.ui.UserSide.Wallet;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.toilets.go.R;
import com.toilets.go.databinding.FragmentWalletBinding;
import com.toilets.go.ui.UserSide.HomeUserAct;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;
import com.toilets.go.utills.Util;
import com.toilets.go.utills.craditcard.SubmitCreditCardActivity;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {
private WalletViewModel walletViewModel ;
private FragmentWalletBinding binding;
private   WalletAdapter walletAdapter;
    Session session;

private ArrayList<WalletResponse.Result> resultArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_wallet, container, false);
        walletViewModel =new ViewModelProvider(this).get(WalletViewModel.class);
        session = new Session(requireActivity());
binding.edtPrice.setText(session.getWALLET_BALANCE());
        walletAdapter = new WalletAdapter(requireContext(),resultArrayList);
        binding.payHistory.setHasFixedSize(true);
        binding.payHistory.setAdapter(walletAdapter);
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        getMovieArticles();
binding.imgHeader.setOnClickListener(v -> {
 /*  requireActivity().startActivity(new Intent(requireContext(), SubmitCreditCardActivity.class));
    walletViewModel.*/
    openBottemSheetfor();
});

        return binding.getRoot();
    }

    private void openBottemSheetfor() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations =
                android.R.style.Widget_Material_PopupWindow;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable
                (Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_money);
        dialog.setCancelable(true);
        EditText  edtEmail = dialog.findViewById(R.id.edtEmail);
        Button btn_google = dialog.findViewById(R.id.btn_google);
         btn_google.setOnClickListener(v -> {
             String value = edtEmail.getText().toString().trim();
             if (value.equalsIgnoreCase("")){
                 edtEmail.setError(getString(R.string.empty));
             }else {
                 DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
                 walletViewModel.getAddMoney(value,session.getUserId(),session.getAuthtoken()).
                         observe(getViewLifecycleOwner(),walletResponse -> {
                     Log.e("TAG", "openBottemSheetfor: "+walletResponse.toString());
                     session.setWALLET_BALANCE(walletResponse.result.wallet);
                             binding.edtPrice.setText(session.getWALLET_BALANCE());
                             dialog.dismiss();
                     getMovieArticles();
                     DataManager.getInstance().hideProgressMessage();

                 });
             }

         });
        dialog.show();

    }
    private void getMovieArticles() {
        walletViewModel.getWalletResponseLiveData().observe(getViewLifecycleOwner(), articleResponse -> {
            if (articleResponse != null) {
                List<WalletResponse.Result> articles = articleResponse.result;
                resultArrayList.addAll(articles);
                walletAdapter.notifyDataSetChanged();
                DataManager.getInstance().hideProgressMessage();
                Log.e("TAG", "getMovieArticles: "+resultArrayList.size() );
            }
        });
    }


}