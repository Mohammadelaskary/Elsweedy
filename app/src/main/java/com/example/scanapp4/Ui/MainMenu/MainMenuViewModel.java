package com.example.scanapp4.Ui.MainMenu;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.room.Room;

import com.example.scanapp4.DatabaseClasses.AppDatabase;
import com.example.scanapp4.DatabaseClasses.PalletsDao;
import com.example.scanapp4.Model.Pallet;
import com.example.scanapp4.Model.PalletWithBoxes;
import com.example.scanapp4.R;
import com.example.scanapp4.SingleLiveEvent;
import com.example.scanapp4.Model.Status;
import com.example.scanapp4.Model.StatusWithMessage;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainMenuViewModel extends AndroidViewModel {

    private PalletsDao palletsDao;
    public MainMenuViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "database-name").fallbackToDestructiveMigration().build();
        palletsDao = db.palletsDao();
    }

    private final SingleLiveEvent<StatusWithMessage> deletePalletsStatus = new SingleLiveEvent<>();

    public SingleLiveEvent<StatusWithMessage> getDeletePalletsStatus() {
        return deletePalletsStatus;
    }

    public void deletePallets(){
        palletsDao.deleteAllPallets().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        deletePalletsStatus.postValue(new StatusWithMessage(Status.LOADING,""));
                    }

                    @Override
                    public void onComplete() {
                        deletePalletsStatus.postValue(new StatusWithMessage(Status.SUCCESS,getApplication().getString(R.string.cleared_successfully)));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        deletePalletsStatus.postValue(new StatusWithMessage(Status.ERROR,getApplication().getString(R.string.error_in_clearing_data)));
                    }
                });
    }

    private final SingleLiveEvent<StatusWithMessage> deleteBoxesStatus = new SingleLiveEvent<>();

    public SingleLiveEvent<StatusWithMessage> getDeleteBoxesStatus() {
        return deletePalletsStatus;
    }

    public void deleteAllBoxes(){
        palletsDao.deleteAllBoxes().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        deleteBoxesStatus.postValue(new StatusWithMessage(Status.LOADING,""));
                    }

                    @Override
                    public void onComplete() {
                        deleteBoxesStatus.postValue(new StatusWithMessage(Status.SUCCESS,getApplication().getString(R.string.cleared_successfully)));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        deleteBoxesStatus.postValue(new StatusWithMessage(Status.ERROR,getApplication().getString(R.string.error_in_clearing_data)));
                    }
                });
    }
    private final SingleLiveEvent<StatusWithMessage> getPalletsStatus = new SingleLiveEvent<>();

    public SingleLiveEvent<List<PalletWithBoxes>> getPalletsLiveData() {
        return getPalletsLiveData;
    }

    private final SingleLiveEvent<List<PalletWithBoxes>> getPalletsLiveData = new SingleLiveEvent<>();

    public SingleLiveEvent<StatusWithMessage> getPalletsStatus() {
        return getPalletsStatus;
    }

    public void getPallets(String type){
        palletsDao.getAllPalletsWithType(type)
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .subscribe(new SingleObserver<List<PalletWithBoxes>>() {
                @Override
                public void onSubscribe(io.reactivex.disposables.Disposable d) {
                    getPalletsStatus.postValue(new StatusWithMessage(Status.LOADING));
                }

                @Override
                public void onSuccess(List<PalletWithBoxes> palletWithBoxes) {
                    getPalletsStatus.postValue(new StatusWithMessage(Status.SUCCESS));
                    getPalletsLiveData.postValue(palletWithBoxes);
                }

                @Override
                public void onError(Throwable e) {
                    getPalletsStatus.postValue(new StatusWithMessage(Status.ERROR));
                }
            });
    }
    private final SingleLiveEvent<StatusWithMessage> getUpExportedPalletsStatus = new SingleLiveEvent<>();

    public SingleLiveEvent<StatusWithMessage> getUpExportedPalletsStatus() {
        return getUpExportedPalletsStatus;
    }

    public void getUnExportedPallets() {
        palletsDao.getUnExportedPallets()
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .subscribe(new SingleObserver<List<PalletWithBoxes>>() {
                @Override
                public void onSubscribe(io.reactivex.disposables.Disposable d) {
                    getUpExportedPalletsStatus.postValue(new StatusWithMessage(Status.LOADING));
                }

                @Override
                public void onSuccess(List<PalletWithBoxes> palletWithBoxes) {
                    getUpExportedPalletsStatus.postValue(new StatusWithMessage(Status.SUCCESS));
                }

                @Override
                public void onError(Throwable e) {
                    getUpExportedPalletsStatus.postValue(new StatusWithMessage(Status.ERROR));
                }
            });
    }
    private final SingleLiveEvent<StatusWithMessage> updatePalletsStatus = new SingleLiveEvent<>();

    public SingleLiveEvent<StatusWithMessage> getUpdatePalletsStatus() {
        return updatePalletsStatus;
    }
    public void setExportedPallets(List<Pallet> pallets) {
        palletsDao.updatePallets(pallets)
            .subscribeOn(Schedulers.io())
            .subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                    updatePalletsStatus.postValue(new StatusWithMessage(Status.LOADING));
                }

                @Override
                public void onComplete() {
                    updatePalletsStatus.postValue(new StatusWithMessage(Status.SUCCESS));
                }

                @Override
                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                    updatePalletsStatus.postValue(new StatusWithMessage(Status.ERROR));
                }
            });
    }
}