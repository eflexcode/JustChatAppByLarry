package com.example.justchat.viewmodle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DateViewModel extends ViewModel {

   public MutableLiveData<String > myDate = new MutableLiveData<>();

   public LiveData<String> liveDataDate(){
        return myDate;
    }

}
