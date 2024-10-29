package com.example.scanapp4.Ui.DeletePallet;

import static io.reactivex.schedulers.Schedulers.io;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
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


import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class DeletePalletViewModel extends AndroidViewModel {
    private PalletsDao palletsDao;
    public DeletePalletViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "database-name").fallbackToDestructiveMigration().build();
        palletsDao = db.palletsDao();
    }

    private SingleLiveEvent<StatusWithMessage> getPalletStatus = new SingleLiveEvent<>();
    private SingleLiveEvent<PalletWithBoxes> getPalletLiveData = new SingleLiveEvent<>();

    public SingleLiveEvent<StatusWithMessage> getGetPalletStatus() {
        return getPalletStatus;
    }

    public SingleLiveEvent<PalletWithBoxes> getGetPalletLiveData() {
        return getPalletLiveData;
    }

    public void getPalletData(String palletCode){
        palletsDao.getPalletByPalletCode(palletCode)
                        .subscribeOn(Schedulers.io())
                                .subscribe(new SingleObserver<PalletWithBoxes>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        getPalletStatus.postValue(new StatusWithMessage(Status.LOADING));
                                    }

                                    @Override
                                    public void onSuccess(PalletWithBoxes pallet) {
                                        getPalletStatus.postValue(new StatusWithMessage(Status.SUCCESS));
                                        getPalletLiveData.postValue(pallet);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        getPalletStatus.postValue(new StatusWithMessage(Status.ERROR, getApplication().getString(R.string.error)));
                                    }
                                });

    }
    private SingleLiveEvent<StatusWithMessage> deletePalletStatus = new SingleLiveEvent<>();

    public SingleLiveEvent<StatusWithMessage> getDeletePalletStatus() {
        return deletePalletStatus;
    }

    public void deleteScannedPallet(Pallet pallet){
        palletsDao.deletePallet(pallet)
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(io.reactivex.rxjava3.disposables.@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        deletePalletStatus.postValue(new StatusWithMessage(Status.LOADING));
                    }

                    @Override
                    public void onComplete() {
                        deletePalletStatus.postValue(new StatusWithMessage(Status.SUCCESS,getApplication().getString(R.string.deleted_successfully)));

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        deletePalletStatus.postValue(new StatusWithMessage(Status.ERROR,getApplication().getString(R.string.error)));

                    }
                });
    }
    private SingleLiveEvent<StatusWithMessage> deleteBoxStatus = new SingleLiveEvent<>();

    public SingleLiveEvent<StatusWithMessage> getDeleteBoxStatus() {
        return deleteBoxStatus;
    }
    public void deleteBox(Box box) {
        palletsDao.deleteBox(box)
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(io.reactivex.rxjava3.disposables.@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        deleteBoxStatus.postValue(new StatusWithMessage(Status.LOADING));
                    }

                    @Override
                    public void onComplete() {
                        deleteBoxStatus.postValue(new StatusWithMessage(Status.SUCCESS,getApplication().getString(R.string.deleted_successfully)));

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        deleteBoxStatus.postValue(new StatusWithMessage(Status.ERROR,getApplication().getString(R.string.error)));

                    }
                });
    }
}