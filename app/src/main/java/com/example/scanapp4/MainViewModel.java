package com.example.scanapp4;

import android.app.Application;

import androidx.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.room.Room;


import com.example.scanapp4.DatabaseClasses.AppDatabase;
import com.example.scanapp4.DatabaseClasses.ItemsDao;
import com.example.scanapp4.Model.Status;
import com.example.scanapp4.Model.StatusWithMessage;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private ItemsDao itemsDao;
    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "database-name").build();
        itemsDao = db.itemsDao();
    }

    private final SingleLiveEvent<StatusWithMessage> saveItemsStatus = new SingleLiveEvent<>();
    public void insertItem(Item item){
        itemsDao.insertItem(item).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        saveItemsStatus.postValue(new StatusWithMessage(Status.LOADING,""));
                    }

                    @Override
                    public void onComplete() {
                        saveItemsStatus.postValue(new StatusWithMessage(Status.SUCCESS,"Saved successfully"));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        saveItemsStatus.postValue(new StatusWithMessage(Status.ERROR,"Error in saving data"));
                    }
                });
    }
    public void updateItem(Item item){
        itemsDao.updateItem(item).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        saveItemsStatus.postValue(new StatusWithMessage(Status.LOADING,""));
                    }

                    @Override
                    public void onComplete() {
                        saveItemsStatus.postValue(new StatusWithMessage(Status.SUCCESS,"Saved successfully"));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        saveItemsStatus.postValue(new StatusWithMessage(Status.ERROR,"Error in saving data"));
                    }
                });
    }

    public SingleLiveEvent<StatusWithMessage> getSaveItemsStatus() {
        return saveItemsStatus;
    }

    private final SingleLiveEvent<StatusWithMessage> getItemStatus = new SingleLiveEvent<>();
    private final SingleLiveEvent<Item> itemFound = new SingleLiveEvent<>();
    public void getItemStatus(String code) {
        itemsDao.getItem(code).subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.newThread())
                .subscribe(new SingleObserver<Item>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        getItemStatus.postValue(new StatusWithMessage(Status.LOADING));
                    }

                    @Override
                    public void onSuccess(Item item) {
                        getItemStatus.postValue(new StatusWithMessage(Status.SUCCESS));
                        itemFound.postValue(item);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getItemStatus.postValue(new StatusWithMessage(Status.ERROR));
                    }
                });
    }

    public SingleLiveEvent<Item> getItemFound() {
        return itemFound;
    }

    public SingleLiveEvent<StatusWithMessage> getGetItemStatus() {
        return getItemStatus;
    }

    private SingleLiveEvent<List<Item>> getAllItemsLiveData = new SingleLiveEvent<>();
    private SingleLiveEvent<StatusWithMessage> getAllItemsStatus = new SingleLiveEvent<>();

    public void getAllItems(){
        itemsDao.getAll().subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.newThread())
                .subscribe(new SingleObserver<List<Item>>() {
                    @Override
                    public void onSubscribe(io.reactivex.disposables.Disposable d) {
                        getAllItemsStatus.postValue(new StatusWithMessage(Status.LOADING));
                    }

                    @Override
                    public void onSuccess(List<Item> items) {
                        getAllItemsLiveData.postValue(items);
                        getAllItemsStatus.postValue(new StatusWithMessage(Status.SUCCESS));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getAllItemsStatus.postValue(new StatusWithMessage(Status.ERROR));
                    }
                });

    }

    public SingleLiveEvent<List<Item>> getGetAllItemsLiveData() {
        return getAllItemsLiveData;
    }


    private SingleLiveEvent<StatusWithMessage> deleteAllItemsStatus = new SingleLiveEvent<>();

    public void deleteAllItems(){
        itemsDao.deleteAll().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        deleteAllItemsStatus.postValue(new StatusWithMessage(Status.LOADING));
                    }

                    @Override
                    public void onComplete() {
                        deleteAllItemsStatus.postValue(new StatusWithMessage(Status.SUCCESS,"Cleared successfully"));
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        deleteAllItemsStatus.postValue(new StatusWithMessage(Status.ERROR,"Error in resetting data"));
                    }
                });

    }

    public SingleLiveEvent<StatusWithMessage> getDeleteAllItemsStatus() {
        return deleteAllItemsStatus;
    }
}
