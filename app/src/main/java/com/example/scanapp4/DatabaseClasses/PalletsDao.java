package com.example.scanapp4.DatabaseClasses;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.scanapp4.Model.Box;
import com.example.scanapp4.Model.Pallet;
import com.example.scanapp4.Model.PalletWithBoxes;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface PalletsDao {
    @Query("SELECT * FROM pallet")
    Single<List<PalletWithBoxes>> getAllPallets();
    @Query("SELECT * FROM pallet where Exported = false")
    Single<List<PalletWithBoxes>> getUnExportedPallets();

    @Query("SELECT * FROM pallet Where `Code Type` = :type")
    Single<List<PalletWithBoxes>> getAllPalletsWithType(String type);
    @Query("SELECT * FROM pallet where `Pallet Code` = :palletCode")
    Single<PalletWithBoxes> getPalletByPalletCode(String palletCode);
    @Query("SELECT * FROM pallet where `Pallet Code` = :palletCode")
    Single<Pallet> checkPalletCode(String palletCode);
    @Query("Delete FROM pallet")
    Completable deleteAllPallets();
    @Query("Delete FROM box")
    Completable deleteAllBoxes();
    @Insert
    Single<Long> insertPallet(Pallet pallet);
    @Insert
    Completable insertBoxes(List<Box> boxes);
    @Update
    Completable updatePallets(List<Pallet> pallets);
    @Delete
    Completable deletePallet(Pallet pallet);
    @Delete
    Completable deleteBox(Box box);
}
