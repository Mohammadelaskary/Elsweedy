package com.example.scanapp4.Ui.AddPallets;

import static android.content.ContentValues.TAG;
import static io.reactivex.schedulers.Schedulers.*;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.room.Room;

import com.example.scanapp4.DatabaseClasses.AppDatabase;
import com.example.scanapp4.DatabaseClasses.PalletsDao;
import com.example.scanapp4.Model.Box;
import com.example.scanapp4.Model.Pallet;
import com.example.scanapp4.Model.PalletWithBoxes;
import com.example.scanapp4.Model.Status;
import com.example.scanapp4.Model.StatusWithMessage;
import com.example.scanapp4.R;
import com.example.scanapp4.SingleLiveEvent;

import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class AddNewPalletsViewModel extends AndroidViewModel {
    private PalletsDao palletsDao;

    public AddNewPalletsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "database-name")
                .fallbackToDestructiveMigration()
                .build();
        palletsDao = db.palletsDao();
    }

    private SingleLiveEvent<StatusWithMessage> checkPalletCodeStatus = new SingleLiveEvent<>();
    private SingleLiveEvent<Pallet> palletSingleLiveEvent = new SingleLiveEvent<>();
    public void checkPalletCode (String palletCode){
        palletsDao.checkPalletCode(palletCode)
                .subscribeOn(io())
                .subscribe(new SingleObserver<Pallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        checkPalletCodeStatus.postValue(new StatusWithMessage(Status.LOADING));
                        Log.d(TAG, "checkPalletOnSubscribe: ");
                    }

                    @Override
                    public void onSuccess(Pallet pallet) {
                        checkPalletCodeStatus.postValue(new StatusWithMessage(Status.SUCCESS));
                        palletSingleLiveEvent.postValue(pallet);
                        Log.d(TAG, "checkPalletOnSuccess: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        checkPalletCodeStatus.postValue(new StatusWithMessage(Status.ERROR,getApplication().getString(R.string.error)));
                        Log.d(TAG, "checkPalletOnError: ");
                    }
                });
    }

    public SingleLiveEvent<StatusWithMessage> getCheckPalletCodeStatus() {
        return checkPalletCodeStatus;
    }


    public SingleLiveEvent<Pallet> getPalletSingleLiveEvent() {
        return palletSingleLiveEvent;
    }

    public SingleLiveEvent<StatusWithMessage> getSavePalletDataStatus() {
        return savePalletDataStatus;
    }

    private SingleLiveEvent<StatusWithMessage> savePalletDataStatus = new SingleLiveEvent<>();
    private SingleLiveEvent<Long> getPalletId = new SingleLiveEvent<>();
    public void savePalletData(Pallet pallet) {
        palletsDao.insertPallet(pallet)
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        savePalletDataStatus.postValue(new StatusWithMessage(Status.LOADING));
                    }

                    @Override
                    public void onSuccess(Long id) {
                        getPalletId.postValue(id);
                        savePalletDataStatus.postValue(new StatusWithMessage(Status.SUCCESS));
                    }

                    @Override
                    public void onError(Throwable e) {
                        savePalletDataStatus.postValue(new StatusWithMessage(Status.ERROR, getApplication().getString(R.string.error)));
                        Log.d(TAG, "onErrorSavePallet: "+e.getMessage());
                    }
                });
    }

    public SingleLiveEvent<Long> getGetPalletId() {
        return getPalletId;
    }


    private SingleLiveEvent<StatusWithMessage> saveBoxesDataStatus = new SingleLiveEvent<>();
    public void saveBoxes(List<Box> boxes) {
        palletsDao.insertBoxes(boxes)
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(io.reactivex.rxjava3.disposables.@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        saveBoxesDataStatus.postValue(new StatusWithMessage(Status.LOADING));
                    }

                    @Override
                    public void onComplete() {
                        saveBoxesDataStatus.postValue(new StatusWithMessage(Status.SUCCESS,getApplication().getString(R.string.saved_successfully)));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        saveBoxesDataStatus.postValue(new StatusWithMessage(Status.ERROR,getApplication().getString(R.string.error)));
                        Log.d(TAG, "onErrorSaveBoxes: "+e.getMessage());
                    }
                });
    }

    public SingleLiveEvent<StatusWithMessage> getSaveBoxesDataStatus() {
        return saveBoxesDataStatus;
    }
}