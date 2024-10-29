package com.example.scanapp4;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "code")
    public String code;

    @ColumnInfo(name = "qty")
    public int qty;

    public Item(String code, int qty) {
        this.code = code;
        this.qty = qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    String toLine(){
        return code+","+qty+",";
    }
}
