package com.example.scanapp4.DatabaseClasses;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.scanapp4.Item;
import com.example.scanapp4.Model.Box;
import com.example.scanapp4.Model.Pallet;

@Database(entities = {Item.class, Pallet.class, Box.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ItemsDao itemsDao();
    public abstract PalletsDao palletsDao();
}
