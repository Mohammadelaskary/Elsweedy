package com.example.scanapp4.DatabaseClasses;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.scanapp4.Item;
import com.example.scanapp4.Model.Pallet;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface ItemsDao {
    @Query("SELECT * FROM item")
    Single<List<Item>> getAll();

    @Query("SELECT * FROM item WHERE code = :code")
    Single<Item> getItem(String code);

    @Query("Delete FROM item")
    Completable deleteAll();
    @Insert
    Completable insertItem (Item item);
    @Update
    Completable updateItem (Item item);

}
