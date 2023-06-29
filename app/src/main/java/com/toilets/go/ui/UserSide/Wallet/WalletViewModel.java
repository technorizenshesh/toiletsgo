package com.toilets.go.ui.UserSide.Wallet;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.toilets.go.models.AddWalletResponse;
import com.toilets.go.utills.Session;

public class WalletViewModel  extends AndroidViewModel {
     WalletRepository walletRepository;
     LiveData<WalletResponse> walletResponseLiveData;
     LiveData<AddWalletResponse> addmoney;
Session session ;
    public WalletViewModel(@NonNull Application application) {
        super(application);
        walletRepository = new WalletRepository();
         session = new Session(application);
        getwalletResponse();
    }
public void getwalletResponse(){
    walletResponseLiveData=  walletRepository.getMovieArticles(session.getUserId(), session.getAuthtoken());
}

    public LiveData<AddWalletResponse> getAddMoney(String amount, String user_id, String auth) {
        this.addmoney = walletRepository.getAddMoney(amount,user_id,auth);
        return addmoney;
    }

}