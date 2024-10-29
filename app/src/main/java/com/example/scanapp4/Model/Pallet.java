package com.example.scanapp4.Model;

import static com.example.scanapp4.Tools.PalletType.GENERAL_CUSTOMER;
import static com.example.scanapp4.Tools.PalletType.LENOI_CUSTOMER;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Pallet {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "Pallet Code")
    private String palletCode;

    @ColumnInfo(name = "Boxes Codes")
    private String boxesCodes;
    @ColumnInfo(name = "Code Length")
    private String length;
    @ColumnInfo(name = "Code Type")
    private String codeType;
    @ColumnInfo(name = "Exported")
    private boolean exported = false;

    public Pallet(String palletCode, String codeType) {
        this.palletCode = palletCode;
        if (codeType.equals(LENOI_CUSTOMER)){
            if (palletCode.length()>49){
                length = palletCode.substring(51,55);
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

    public String getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(String palletCode) {
        this.palletCode = palletCode;
    }

    public String getBoxesCodes() {
        return boxesCodes;
    }

    public void setBoxesCodes(String boxesCodes) {
        this.boxesCodes = boxesCodes;
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

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }
}
