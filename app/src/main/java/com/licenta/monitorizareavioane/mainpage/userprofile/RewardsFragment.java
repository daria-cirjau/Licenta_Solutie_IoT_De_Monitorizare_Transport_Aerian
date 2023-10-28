package com.licenta.monitorizareavioane.mainpage.userprofile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.licenta.monitorizareavioane.R;

public class RewardsFragment extends Fragment {

    private ColorPickerView colorPickerView;
    private ImageView ivSelectedColorPlane;
    private ImageView ivPadlock;
    private Button btnSave;
    private SharedPreferences sharedPreferences;

    public RewardsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rewards, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        colorPickerView = getActivity().findViewById(R.id.rewardsFragmentColorPicker);
        ivSelectedColorPlane = getActivity().findViewById(R.id.rewardsFragmentPlane);
        btnSave = getActivity().findViewById(R.id.rewardsFragmentBtnSave);
        ivPadlock = getActivity().findViewById(R.id.rewardsFragmentPadlock);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        setSaveButton();
        setColorPickerListener();
    }

    private void setSaveButton() {
        if (UserProfileFragment.getNoMeters() < 3000) {
            btnSave.setClickable(false);
            btnSave.setOnClickListener(null);
            ivPadlock.setImageDrawable(getResources().getDrawable(R.drawable.padlock));
        } else {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveColorToSharedPrefs(colorPickerView.getSelectedColor());
                    Toast.makeText(getContext(), "The color was saved. Please return to the map and wait for it to refresh", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setColorPickerListener() {
        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int selectedColor) {
                ColorFilter colorFilter = new LightingColorFilter(selectedColor, Color.BLACK);
                ivSelectedColorPlane.setColorFilter(colorFilter);
            }
        });
    }

    private void saveColorToSharedPrefs(int selectedColor) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedPlaneColor", selectedColor);
        editor.apply();
    }

}