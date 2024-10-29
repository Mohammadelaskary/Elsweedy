package com.example.scanapp4.Ui.DeletePallet;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scanapp4.Model.Box;
import com.example.scanapp4.databinding.BoxItemLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class BoxesCodesAdapter extends RecyclerView.Adapter<BoxesCodesAdapter.BoxesCodesViewHolder> {
    private List<Box> boxesCodes = new ArrayList<>();

    public void setBoxesCodes(List<Box> boxesCodes) {
        this.boxesCodes = boxesCodes;
        notifyDataSetChanged();
    }

    private OnBoxItemDeleteButtonClicked onBoxItemDeleteButtonClicked;
    public BoxesCodesAdapter(OnBoxItemDeleteButtonClicked onBoxItemDeleteButtonClicked) {
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
        holder.binding.palletCode.setText(boxesCodes.get(position).getBoxNumber());
        holder.binding.removePallet.setOnClickListener(view -> {
            onBoxItemDeleteButtonClicked.onBoxItemDeleteButtonClicked(boxesCodes.get(position),position);
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
        void onBoxItemDeleteButtonClicked(Box box,int position);
    }
}
