package com.example.foodhunter.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodhunter.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ModalBottomScreen extends BottomSheetDialogFragment {

    BottomListener mListener;
    private TextView txtCamera, txtGallery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bottomSheet = inflater.inflate(R.layout.modal_layout,container,false);
        txtCamera = bottomSheet.findViewById(R.id.txtOpenCamera);
        txtGallery = bottomSheet.findViewById(R.id.txtChooseGallery);
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked(R.string.choose_to_open_camera);
                dismiss();
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onButtonClicked(R.string.choose_to_select_from_gallery);
                dismiss();
            }
        });
        return bottomSheet;
    }

    public interface BottomListener {
        void onButtonClicked(int text);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        mListener = (BottomListener) childFragment;
    }
}
