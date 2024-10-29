package com.example.scanapp4.Model;

import static com.example.scanapp4.Tools.PalletType.GENERAL_CUSTOMER;
import static com.example.scanapp4.Tools.PalletType.LENOI_CUSTOMER;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PalletWithBoxes {
    @Embedded
    private Pallet pallet;

    @Relation(parentColumn = "id", entityColumn = "Pallet id")
    private List<Box> boxes;

    public PalletWithBoxes(Pallet pallet, List<Box> boxes) {
        this.pallet = pallet;
        this.boxes = boxes;
    }

    public String toLine(){
        StringBuilder builder = new StringBuilder();
        for(Box box:boxes){
            if (pallet.getCodeType().equals(LENOI_CUSTOMER)){
                builder.append(pallet.getId()).append(",")
                    .append(pallet.getPalletCode()).append(",")
                    .append(pallet.getLength()).append(",")
                    .append(box.getId()).append(",")
                    .append(box.getBoxNumber()).append(",")
                    .append(pallet.getLength()).append(",")
                    .append("\n");
            } else {
                builder.append(pallet.getId()).append(",")
                    .append(pallet.getPalletCode()).append(",")
                    .append(box.getId()).append(",")
                    .append(box.getBoxNumber()).append(",")
                    .append("\n");
            }
        }
        return builder.toString();
    }

    // Getters and Setters
    public Pallet getPallet() {
        return pallet;
    }

    public void setPallet(Pallet pallet) {
        this.pallet = pallet;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }
}

