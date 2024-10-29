package com.example.scanapp4.Ui.AddPallets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanapp4.Model.Box;
import com.example.scanapp4.databinding.BoxItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class BoxesCodesAdapter extends RecyclerView.Adapter<BoxesCodesAdapter.BoxesCodesViewHolder> {
    private List<String> boxesCodes = new ArrayList<>();
    private OnBoxItemDeleteButtonClicked onBoxItemDeleteButtonClicked;
    public BoxesCodesAdapter(List<String> boxesCodes,OnBoxItemDeleteButtonClicked onBoxItemDeleteButtonClicked) {
        this.boxesCodes = boxesCodes;
        this.onBoxItemDeleteButtonClicked = onBoxItemDeleteButtonClicked;
    }


    @NonNull
    @Override
    public BoxesCodesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BoxItemLayoutBinding binding = BoxItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new BoxesCodesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BoxesCodesViewHolder holder, int position) {
        holder.binding.palletCode.setText(boxesCodes.get(position));
        holder.binding.removePallet.setOnClickListener(view -> {
            onBoxItemDeleteButtonClicked.onBoxItemDeleteButtonClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return boxesCodes.size();
    }

    public class BoxesCodesViewHolder extends RecyclerView.ViewHolder{
        private BoxItemLayoutBinding binding;
        public BoxesCodesViewHolder(@NonNull BoxItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public interface OnBoxItemDeleteButtonClicked {
        void onBoxItemDeleteButtonClicked(int position);
    }
}
