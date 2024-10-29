package com.example.scanapp4.Ui.DeletePallet;

import static com.example.scanapp4.Tools.Tools.attachButtonsToListener;
import static com.example.scanapp4.Tools.Tools.changeFragmentTitle;
import static com.example.scanapp4.Tools.Tools.getEditTextText;
import static com.example.scanapp4.Tools.Tools.showAlertDialog;
import static com.example.scanapp4.Tools.Tools.showBackButton;
import static com.example.scanapp4.Tools.Tools.showSuccessAlerter;
import static com.example.scanapp4.Tools.Tools.warningDialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.scanapp4.Model.Box;
import com.example.scanapp4.Model.PalletWithBoxes;
import com.example.scanapp4.R;
import com.example.scanapp4.Tools.LoadingDialog;
import com.example.scanapp4.Tools.ZebraScanner;
import com.example.scanapp4.databinding.FragmentDeletePalletBinding;

import java.util.ArrayList;
import java.util.List;

public class DeletePalletFragment extends Fragment implements ZebraScanner.OnDataScanned, View.OnClickListener, BoxesCodesAdapter.OnBoxItemDeleteButtonClicked {

    private DeletePalletViewModel viewModel;

    public static DeletePalletFragment newInstance() {
        return new DeletePalletFragment();
    }
    private ZebraScanner barcodeReader;
    private LoadingDialog loadingDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DeletePalletViewModel.class);
        barcodeReader = new ZebraScanner(requireActivity(),this);
        loadingDialog = new LoadingDialog(requireContext());
    }
    private FragmentDeletePalletBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDeletePalletBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attachButtonsToListener(this,binding.deleteWholePallet);
        setUpBoxesRecyclerView();
        observeGettingPalletData();
        observeDeletingPallet();
        observeDeletingBox();
    }
    private BoxesCodesAdapter boxesAdapter;
    private void setUpBoxesRecyclerView() {
        boxesAdapter = new BoxesCodesAdapter(this);
        binding.boxesCodes.setAdapter(boxesAdapter);
    }

    private void clearScreen(){
        scannedPallet = null;
        binding.boxesCodes.setVisibility(View.GONE);
    }
    private void observeDeletingPallet() {
        viewModel.getDeletePalletStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show(); break;
                case SUCCESS:
                    showSuccessAlerter(statusWithMessage.getMessage(),requireActivity());
                    clearScreen();
                    break;
                case ERROR:
                    warningDialog(requireContext(), statusWithMessage.getMessage());
                    break;
            }
        });
    }
    private int removedBoxPosition;
    private void observeDeletingBox() {
        viewModel.getDeleteBoxStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show(); break;
                case SUCCESS:
                    showSuccessAlerter(statusWithMessage.getMessage(),requireActivity());
                    viewModel.getPalletData(getEditTextText(binding.palletCode));
                    break;
                case ERROR:
                    warningDialog(requireContext(), statusWithMessage.getMessage());
                    break;
            }
        });
    }

    private PalletWithBoxes scannedPallet = null;
    private List<Box> palletBoxes = new ArrayList<>();
    private void observeGettingPalletData() {
        viewModel.getGetPalletStatus().observe(requireActivity(),statusWithMessage -> {
            switch (statusWithMessage.getStatus()){
                case LOADING: loadingDialog.show(); break;
                case SUCCESS:
                    loadingDialog.hide(); break;
                case ERROR:
                    loadingDialog.hide();
                    warningDialog(requireContext(),statusWithMessage.getMessage());
                    break;
            }
        });
        viewModel.getGetPalletLiveData().observe(requireActivity(),pallet -> {
            if (pallet!=null){
                scannedPallet = pallet;
                binding.palletCode.getEditText().setText(scannedPallet.getPallet().getPalletCode());
                palletBoxes = scannedPallet.getBoxes();
                boxesAdapter.setBoxesCodes(palletBoxes);
            } else {
                binding.palletCode.setError(getString(R.string.wrong_pallet_code));
            }
        });
    }

    @Override
    public void onDataScanned(@NonNull String data) {
        viewModel.getPalletData(data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.delete_whole_pallet) {
            if (scannedPallet != null) {
                showAlertDialog(
                        requireContext(),
                        getString(R.string.warning),
                        getString(R.string.are_you_sure_to_delete_scanned_pallet),
                        getString(R.string.yes),
                        (dialogInterface, i) -> {
                            viewModel.deleteScannedPallet(scannedPallet.getPallet());
                        },
                        getString(R.string.cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss()
                );
            } else {
                warningDialog(requireContext(), getString(R.string.please_scan_pallet_code));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeReader.onResume();
        showBackButton(requireActivity());
        changeFragmentTitle(getString(R.string.delete_pallet_box),requireActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeReader.onPause();
    }

    @Override
    public void onBoxItemDeleteButtonClicked(Box box,int position) {
            showAlertDialog(
                    requireContext(),
                    getString(R.string.warning),
                    getString(R.string.are_you_sure_you_want_to_delete_that_box),
                    getString(R.string.yes),
                    (dialogInterface, i) -> {
                        viewModel.deleteBox(box);
                        removedBoxPosition = position;
                    },
                    getString(R.string.cancel),
                    (dialogInterface, i) -> dialogInterface.dismiss()
            );
    }
}