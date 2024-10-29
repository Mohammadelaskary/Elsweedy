package com.example.scanapp4.Ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.scanapp4.databinding.ExportExcelSheetDialogLayoutBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ExportExcelSheetDialog extends BottomSheetDialog {
    private OnExportExcelSheetButtonClicked onExportExcelSheetButtonClicked;
    public ExportExcelSheetDialog(@NonNull Context context,OnExportExcelSheetButtonClicked onExportExcelSheetButtonClicked) {
        super(context);
        this.onExportExcelSheetButtonClicked = onExportExcelSheetButtonClicked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExportExcelSheetDialogLayoutBinding binding = ExportExcelSheetDialogLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.exportGeneralCustomer.setOnClickListener(v->onExportExcelSheetButtonClicked.onExportGeneralCustomer());
        binding.exportLenoiCustomer.setOnClickListener(v->onExportExcelSheetButtonClicked.onExportLenoiCustomer());
        binding.exportCustom.setOnClickListener(v->onExportExcelSheetButtonClicked.onExportCustomCustomer());
    }

    public interface OnExportExcelSheetButtonClicked {
        void onExportGeneralCustomer();
        void onExportLenoiCustomer();
        void onExportCustomCustomer();
    }
}
