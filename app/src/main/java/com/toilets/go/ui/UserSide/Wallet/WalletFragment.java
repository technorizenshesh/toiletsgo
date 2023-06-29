package com.toilets.go.ui.UserSide.Wallet;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.toilets.go.R;
import com.toilets.go.databinding.FragmentWalletBinding;
import com.toilets.go.databinding.FullScreenDialogBinding;
import com.toilets.go.utills.DataManager;
import com.toilets.go.utills.Session;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {
    Session session;
    private WalletViewModel walletViewModel;
    private FragmentWalletBinding binding;
    private WalletAdapter walletAdapter;
    private ArrayList<WalletResponse.Result> resultArrayList = new ArrayList<>();
    PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AVwgPhWj4E6imLh10P1EQcLJ1gMHhCVO1IuPxCzOgqJNK7aLx0Z1J-Qa3qYCm7XORVHx1XQiFguZWZUg")
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_wallet, container, false);
        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        session = new Session(requireActivity());
        binding.edtPrice.setText(session.getWALLET_BALANCE());
        walletAdapter = new WalletAdapter(requireContext(), resultArrayList);
        binding.payHistory.setHasFixedSize(true);
        binding.payHistory.setAdapter(walletAdapter);
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
        Intent intent = new Intent(requireActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        requireActivity().startService(intent);
        binding.imgHeader.setOnClickListener(v -> {
            openBottemSheetfor();
        });
        walletViewModel.walletResponseLiveData.observe(getViewLifecycleOwner(), articleResponse -> {
            if (articleResponse != null) {
                List<WalletResponse.Result> articles = articleResponse.result;
                resultArrayList.addAll(articles);
                walletAdapter.notifyDataSetChanged();
                binding.swiperefresh.setRefreshing(false);
                DataManager.getInstance().hideProgressMessage();
                Log.e("TAG", "getMovieArticles: " + resultArrayList.size());
            }
        });
        binding.swiperefresh.setOnRefreshListener(() -> {
            getMovieArticles();

        });
        getMovieArticles();

        return binding.getRoot();
    }


    private void openBottemSheetfor() {
        FullScreenDialogBinding dialogbinding = FullScreenDialogBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(requireActivity(), R.style.NewDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogbinding.getRoot());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        dialog.getWindow().setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white));

        dialog.show();
        dialogbinding.close.setOnClickListener(v2 -> {
            dialog.cancel();
        });

        dialogbinding.pay10.setOnClickListener(v2 -> {
            payWithPaypal("10");

            dialog.cancel();
        });
        dialog.setOnDismissListener(dialog1 -> {
        });

    }

    private void payWithPaypal(String s) {
        try {
             /*  Intent intent = new Intent(requireActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        requireActivity().startService(intent);
*/
            PayPalPayment payment = new PayPalPayment(new BigDecimal("10.00"),
                    "USD", "sample item",
                    PayPalPayment.PAYMENT_INTENT_AUTHORIZE);
            Intent intent = new Intent(requireActivity(), PaymentActivity.class);
            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    private void openBottemSheetfor() {
        final Dialog dialog = new Dialog(requireActivity(),R.style.NewDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        assert window != null;
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        dialog.setContentView(R.layout.dialog_add_money);
        EditText edtEmail = dialog.findViewById(R.id.edtEmail);
        Button btn_google = dialog.findViewById(R.id.btn_google);
        btn_google.setOnClickListener(v -> {
            String value = edtEmail.getText().toString().trim();
            if (value.equalsIgnoreCase("")) {
                edtEmail.setError(getString(R.string.empty));
            } else {
                DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
                walletViewModel.getAddMoney(value, session.getUserId(), session.getAuthtoken()).
                        observe(getViewLifecycleOwner(), walletResponse -> {
                            Log.e("TAG", "openBottemSheetfor: " + walletResponse.toString());
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
*/

    private void getMovieArticles() {
        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));

        walletViewModel.getwalletResponse();
        walletViewModel.walletResponseLiveData.observe(getViewLifecycleOwner(), articleResponse -> {
            if (articleResponse != null) {
                List<WalletResponse.Result> articles = articleResponse.result;
                resultArrayList.addAll(articles);
                walletAdapter.notifyDataSetChanged();
                binding.swiperefresh.setRefreshing(false);
                DataManager.getInstance().hideProgressMessage();
                Log.e("TAG", "getMovieArticles: " + resultArrayList.size());
            }
        });
    }

    @Override
    public void onDestroy() {
        requireActivity().stopService(new Intent(requireActivity(), PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paymentExample", confirm.toJSONObject().toString(4));
                        // TODO: send 'confirm' to your server for verification.
                        // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                        // for more details.
                        DataManager.getInstance().showProgressMessage(requireActivity(), getString(R.string.please_wait));
                        walletViewModel.getAddMoney("10", session.getUserId(), session.getAuthtoken()).
                                observe(getViewLifecycleOwner(), walletResponse -> {
                                    Log.e("TAG", "openBottemSheetfor: " + walletResponse.toString());
                                    session.setWALLET_BALANCE(walletResponse.result.wallet);
                                    binding.edtPrice.setText(session.getWALLET_BALANCE());
                                    getMovieArticles();
                                    DataManager.getInstance().hideProgressMessage();

                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("paymentExample", "an extremely unlikely failure occurred: "+e);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }
}