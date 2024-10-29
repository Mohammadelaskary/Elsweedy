package com.example.scanapp4.Ui.MainMenu;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION.SDK_INT;
import static com.example.scanapp4.Tools.PalletType.GENERAL_CUSTOMER;
import static com.example.scanapp4.Tools.PalletType.LENOI_CUSTOMER;
import static com.example.scanapp4.Tools.Tools.changeFragmentTitle;
import static com.example.scanapp4.Tools.Tools.getTodayDate;
import static com.example.scanapp4.Tools.Tools.hideBackButton;
import static com.example.scanapp4.Tools.Tools.showAlertDialog;
import static com.example.scanapp4.Tools.Tools.showSuccessAlerter;
import static com.example.scanapp4.Tools.Tools.warningDialog;
import static com.example.scanapp4.Ui.AddPallets.AddNewPalletsFragment.SOURCE_KEY;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scanapp4.Model.Pallet;
import com.example.scanapp4.Model.PalletWithBoxes;
import com.example.scanapp4.R;
import com.example.scanapp4.Tools.LoadingDialog;
import com.example.scanapp4.Ui.AddPallets.AddNewPalletsViewModel;
import com.example.scanapp4.Ui.Dialogs.ExportExcelSheetDialog;
import com.example.scanapp4.databinding.FragmentMainMenuBinding;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MainMenuFragment extends Fragment implements View.OnClickListener, ExportExcelSheetDialog.OnExportExcelSheetButtonClicked {

    private MainMenuViewModel viewModel;

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    private FragmentMainMenuBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMainMenuBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    private ExportExcelSheetDialog exportExcelSheetDialog;
    private LoadingDialog loadingDialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        exportExcelSheetDialog = new ExportExcelSheetDialog(requireContext(),this);
        loadingDialog = new LoadingDialog(requireContext());
        viewModel = new ViewModelProvider(this).get(MainMenuViewModel.class);
        attachButtonsToListener();
        observeGettingPallets();
        observeGettingUpExportedPallets();
        observeUpdatingPallets();
        observeClearingDatabase();
    }

    private void observeClearingDatabase() {
        viewModel.getDeletePalletsStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show();break;
                case SUCCESS:
                    viewModel.deleteAllBoxes();
                    break;
                case ERROR:
                    loadingDialog.hide();
                    warningDialog(requireContext(),getString(R.string.error));
                    break;

            }
        });
        viewModel.getDeleteBoxesStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show();break;
                case SUCCESS:
                    loadingDialog.hide();
                    showSuccessAlerter(getString(R.string.cleared_successfully),requireActivity());
                    break;
                case ERROR:
                    loadingDialog.hide();
                    warningDialog(requireContext(),getString(R.string.error));
                    break;

            }
        });
    }

    private void observeUpdatingPallets() {
        viewModel.getUpdatePalletsStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show(); break;
                case SUCCESS:
                    loadingDialog.hide();
                    showSuccessAlerter("Exported successfully",requireActivity());
                    break;
                case ERROR:
                    loadingDialog.hide();
                    warningDialog(requireContext(),getString(R.string.error));
                    break;
            }
        });
    }

    private void observeGettingUpExportedPallets() {
        viewModel.getUpExportedPalletsStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show(); break;
                case SUCCESS:
                    loadingDialog.hide();
                    showAlertDialog(
                            requireContext(),
                            getString(R.string.warning),
                            getString(R.string.there_are_an_not_exported_data_are_you_sure_to_clear_database),
                            getString(R.string.yes),
                            (dialogInterface, i) -> {
                                viewModel.deletePallets();
                                },
                            getString(R.string.cancel),
                            (dialogInterface, i) -> dialogInterface.dismiss()

                    );
                    break;
                case ERROR:
                    viewModel.deletePallets();
                    viewModel.deleteAllBoxes();
                    break;

            }
        });
    }
    private List<PalletWithBoxes> exportPallets = new ArrayList<>();
    private void observeGettingPallets() {
        viewModel.getPalletsStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show(); break;
                case SUCCESS: loadingDialog.dismiss();break;
                case ERROR: {
                    loadingDialog.dismiss();
                    warningDialog(requireContext(),statusWithMessage.getMessage());
                } break;
            }
        });
        viewModel.getPalletsLiveData().observe(requireActivity(),palletWithBoxes -> {
            exportPallets = palletWithBoxes;
            fileData = convertPalletsToExcelData(palletWithBoxes,type);
            fileName = type+"-"+getTodayDate()+".csv";
            requestForStoragePermissions(fileName,fileData);
        });
    }
    private String fileData = "",fileName = "";
    private String convertPalletsToExcelData(List<PalletWithBoxes> palletWithBoxes, String type) {
        StringBuilder data = new StringBuilder();
        if(type.equals(LENOI_CUSTOMER))
            data.append(LenoiFirstRow);
        else
            data.append(GeneralFirstRow);
        for (PalletWithBoxes pallet:palletWithBoxes){
            data.append(pallet.toLine());
        }
        return data.toString();
    }

    public void  writeFileOnInternalStorage(String sFileName, String sBody){
        File dir = new File("/sdcard/Scan data");
        if(!dir.exists()){
            dir.mkdirs();
        }
        try {
            File gpxfile = new File(dir, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            setAllPalletsAsExported();
        } catch (Exception e){
            e.printStackTrace();
            warningDialog(requireContext(),e.getMessage());
        }
    }

    private void setAllPalletsAsExported() {
        List<Pallet> pallets = new ArrayList<>();
        for(PalletWithBoxes palletWithBoxes:exportPallets){
            Pallet pallet = palletWithBoxes.getPallet();
            pallet.setExported(true);
            pallets.add(pallet);
        }
        viewModel.setExportedPallets(pallets);
    }

    private static final int STORAGE_PERMISSION_CODE = 23;
    private ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>(){

                        @Override
                        public void onActivityResult(ActivityResult o) {
                            if(SDK_INT >= Build.VERSION_CODES.R){
                                //Android is 11 (R) or above
                                if(Environment.isExternalStorageManager()){
                                    //Manage External Storage Permissions Granted
                                    writeFileOnInternalStorage(fileName,fileData);
                                    Log.d(TAG, "onActivityResult: Manage External Storage Permissions Granted");
                                }else{
                                }
                            }else{
                                //Below android 11
                                writeFileOnInternalStorage(fileName,fileData);
                            }
                        }
                    });
    private void requestForStoragePermissions(String fileName, String fileData) {

        //Android is 11 (R) or above
        if(SDK_INT >= Build.VERSION_CODES.R){
            if (!Environment.isExternalStorageManager())
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    storageActivityResultLauncher.launch(intent);
                }catch (Exception e){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    storageActivityResultLauncher.launch(intent);
                }
            else {
                writeFileOnInternalStorage(fileName, fileData);
            }
        }else{
            //Below android 11
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{
                            WRITE_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE
            );
        }

    }

    private static final String GeneralFirstRow = "PalletId,PalletCode,BoxId,BoxNumber\n";
    private static final String LenoiFirstRow = "PalletId,PalletCode,PalletCodeLength,BoxId,BoxNumber,BoxNumberLength\n";
    private void attachButtonsToListener() {
        binding.generalCustomer.setOnClickListener(this);
        binding.lenoiCustomer.setOnClickListener(this);
        binding.deletePallet.setOnClickListener(this);
        binding.resetDatabase.setOnClickListener(this);
        binding.exportExcellSheet.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        Bundle bundle = new Bundle();
        switch (id){
            case R.id.general_customer:
                bundle.putString(SOURCE_KEY,GENERAL_CUSTOMER);
                Navigation.findNavController(view).navigate(R.id.action_mainMenuFragment_to_addNewPalletsFragment,bundle);
                break;
            case R.id.lenoi_customer:
                bundle.putString(SOURCE_KEY,LENOI_CUSTOMER);
                Navigation.findNavController(view).navigate(R.id.action_mainMenuFragment_to_addNewPalletsFragment,bundle);
                break;
            case R.id.reset_database:
                viewModel.getUnExportedPallets();
                break;
            case R.id.delete_pallet:
                Navigation.findNavController(view).navigate(R.id.action_mainMenuFragment_to_deletePalletFragment);
                break;
            case R.id.export_excell_sheet:
                exportExcelSheetDialog.show();
                break;

        }
    }


    private String type = null;
    @Override
    public void onExportGeneralCustomer() {
        type = GENERAL_CUSTOMER;
        exportExcelSheetDialog.dismiss();
        viewModel.getPallets(GENERAL_CUSTOMER);
    }

    @Override
    public void onExportLenoiCustomer() {
        type = LENOI_CUSTOMER;
        exportExcelSheetDialog.dismiss();
        viewModel.getPallets(LENOI_CUSTOMER);
    }

    @Override
    public void onExportCustomCustomer() {
        exportExcelSheetDialog.dismiss();

    }

    @Override
    public void onResume() {
        super.onResume();
        hideBackButton(requireActivity());
        changeFragmentTitle(getString(R.string.main_menu),requireActivity());
    }
}