package com.example.scanapp4.Model;

import static com.example.scanapp4.Tools.PalletType.LENOI_CUSTOMER;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class Box {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "Pallet id")
    private int palletId;

    @ColumnInfo(name = "Box number")

    private String boxNumber;
    @ColumnInfo(name = "Code Length")
    private String length;
    @ColumnInfo(name = "Code Type")
    private String codeType;

    public Box(String boxNumber, String codeType) {
        this.boxNumber = boxNumber;
        if (codeType.equals(LENOI_CUSTOMER)){
            if (boxNumber.length()>49){
                length = boxNumber.substring(51,55);
            } else{
                length = null;
            }
        } else {
            length = null;
        }
        this.codeType = codeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public int getPalletId() {
        return palletId;
    }

    public void setPalletId(int palletId) {
        this.palletId = palletId;
    }
}
