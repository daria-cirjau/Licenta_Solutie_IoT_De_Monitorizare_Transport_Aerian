package com.licenta.monitorizareavioane.mainpage.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.monitorizareavioane.R;

public class FavoritesViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageViewPlanePicture;
    private TextView textViewPlaneNumber;
    private TextView textViewLatestFlightDate;
    private TextView textViewLatestFlightLoocation;

    public FavoritesViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewPlanePicture = itemView.findViewById(R.id.mainFragmentImageViewPlanePicture);
        textViewPlaneNumber = itemView.findViewById(R.id.mainPageFragmentTvPlaneNumber);
        textViewLatestFlightDate = itemView.findViewById(R.id.mainPageFragmentTvLatestFlightDate);
        textViewLatestFlightLoocation = itemView.findViewById(R.id.mainPageFragmentTvLatestFlightLocation);
    }

    public ImageView getImageViewPlanePicture() {
        return imageViewPlanePicture;
    }

    public void setImageViewPlanePicture(ImageView imageViewPlanePicture) {
        this.imageViewPlanePicture = imageViewPlanePicture;
    }

    public TextView getTextViewPlaneNumber() {
        return textViewPlaneNumber;
    }


    public TextView getTextViewLatestFlightDate() {
        return textViewLatestFlightDate;
    }


    public TextView getTextViewLatestFlightLoocation() {
        return textViewLatestFlightLoocation;
    }

}

