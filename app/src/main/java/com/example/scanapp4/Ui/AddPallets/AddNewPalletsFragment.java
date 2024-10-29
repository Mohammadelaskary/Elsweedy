package com.example.scanapp4.Ui.AddPallets;

import static android.content.ContentValues.TAG;
import static com.example.scanapp4.Tools.Tools.changeFragmentTitle;
import static com.example.scanapp4.Tools.Tools.getEditTextText;
import static com.example.scanapp4.Tools.Tools.showBackButton;
import static com.example.scanapp4.Tools.Tools.showSuccessAlerter;
import static com.example.scanapp4.Tools.Tools.warningDialog;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scanapp4.Model.Box;
import com.example.scanapp4.Model.Pallet;
import com.example.scanapp4.R;
import com.example.scanapp4.Tools.LoadingDialog;
import com.example.scanapp4.Tools.ZebraScanner;
import com.example.scanapp4.databinding.FragmentAddNewPalletsBinding;

import java.util.ArrayList;
import java.util.List;

public class AddNewPalletsFragment extends Fragment implements View.OnClickListener, ZebraScanner.OnDataScanned, BoxesCodesAdapter.OnBoxItemDeleteButtonClicked {

    public static final String SOURCE_KEY = "source_key";
    AddNewPalletsViewModel viewModel;
    private String source;

    public static AddNewPalletsFragment newInstance() {
        return new AddNewPalletsFragment();
    }

    private FragmentAddNewPalletsBinding binding;
    private ZebraScanner barcodeReader;
    private List<String> boxesCodes = new ArrayList<>();
    private List<Box> boxes = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcodeReader = new ZebraScanner(requireActivity(),this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNewPalletsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    public void clearFields(){
        binding.palletCode.getEditText().setText("");
        binding.boxCode.getEditText().setText("");
        boxesCodes.clear();
        boxesCodesAdapter.notifyDataSetChanged();
    }
    private LoadingDialog loadingDialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddNewPalletsViewModel.class);
        loadingDialog = new LoadingDialog(requireContext());
        source = getArguments().getString(SOURCE_KEY);
        setUpBoxesRecyclerView();
        attachButtonsToOnClickListener();
        observeCheckingPalletCode();
        observeSavingPallet();
        observeSavingBoxes();
    }

    private void observeSavingBoxes() {
        viewModel.getSaveBoxesDataStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show();
                    break;
                case SUCCESS:
                    loadingDialog.hide();
                    showSuccessAlerter(statusWithMessage.getMessage(),requireActivity());
                    clearFields();
                    break;
                case ERROR:
                    loadingDialog.hide();
                    warningDialog(requireContext(),statusWithMessage.getMessage());
            }
        });
    }

    private void observeSavingPallet() {
        viewModel.getSavePalletDataStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show();
                break;
                case SUCCESS:
                    loadingDialog.hide();
                    break;
                case ERROR:
                    loadingDialog.hide();
                    warningDialog(requireContext(),statusWithMessage.getMessage());
            }
        });
        viewModel.getGetPalletId().observe(requireActivity(),palletId -> {
            for (String boxCode:boxesCodes){
                Box box = new Box(boxCode,source);
                box.setPalletId(Math.toIntExact(palletId));
                boxes.add(box);
            }
            viewModel.saveBoxes(boxes);
        });
    }

    private void observeCheckingPalletCode() {
        viewModel.getCheckPalletCodeStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show();
                break;
                case SUCCESS: loadingDialog.hide();
                break;
                case ERROR:
                    loadingDialog.hide();
                    binding.palletCode.getEditText().setText(scannedCode);
                    Log.d(TAG, "checkPalletObserveCheckingPalletCode: "+scannedCode);
//                    warningDialog(requireContext(),getString(R.string.error));
                break;
            }
        });
        viewModel.getPalletSingleLiveEvent().observe(requireActivity(),pallet -> {
            if (pallet==null){
                binding.palletCode.getEditText().setText(scannedCode);
            } else {
                warningDialog(requireContext(),"Pallet code added before!\n Please scan different pallet code.");
            }
        });
    }

    private void attachButtonsToOnClickListener() {
        binding.savePallet.setOnClickListener(this);
        binding.clearFields.setOnClickListener(this);
    }

    private BoxesCodesAdapter boxesCodesAdapter;
    private void setUpBoxesRecyclerView() {
        boxesCodesAdapter = new BoxesCodesAdapter(boxesCodes,this);
        binding.boxesCodes.setAdapter(boxesCodesAdapter);
    }
    private String scannedCode = "";
    @Override
    public void onDataScanned(@NonNull String data) {
        scannedCode = data;
            if (binding.palletCode.getEditText().getText().toString().trim().isEmpty()){
            viewModel.checkPalletCode(data);
        } else {
            if (!boxesCodes.contains(data)){
                boxesCodes.add(data);
                boxesCodesAdapter.notifyItemInserted(boxesCodes.size()-1);
            } else {
                warningDialog(requireContext(),getString(R.string.box_code_added_before));
            }
        }
    }

    @Override
    public void onBoxItemDeleteButtonClicked(int position) {
        boxesCodes.remove(position);
        boxesCodesAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.save_pallet:
                String palletCode = getEditTextText(binding.palletCode);
                if (!palletCode.isEmpty()){
                    if (!boxesCodes.isEmpty()){
                        Pallet pallet = new Pallet(palletCode,source);
                        viewModel.savePalletData(pallet);
                    } else {
                       warningDialog(requireContext(),getString(R.string.please_scan_boxes));
                    }
                } else {
                    binding.palletCode.setError(getString(R.string.please_scan_pallet_code));
                }
                break;
            case R.id.clear_fields:
                clearFields();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeReader.onResume();
        showBackButton(requireActivity());
        changeFragmentTitle(getString(R.string.add_pallets),requireActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeReader.onPause();
    }
}