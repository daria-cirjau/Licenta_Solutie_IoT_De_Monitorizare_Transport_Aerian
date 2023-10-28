package com.licenta.monitorizareavioane.mainpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.licenta.monitorizareavioane.R;

public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View infoWindowContentsView;
    private Context context;
    private TextView tvCallsign;
    private TextView tvLat;
    private TextView tvLng;
    private TextView tvTo;
    private TextView tvFrom;

    public CustomWindowAdapter(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        infoWindowContentsView = layoutInflater.inflate(R.layout.cv_info_window, null);

        tvCallsign = infoWindowContentsView.findViewById(R.id.customAdapterTvPlaneNumber);
        tvLat = infoWindowContentsView.findViewById(R.id.customAdapterTvLatValue);
        tvLng = infoWindowContentsView.findViewById(R.id.customAdapterTvLngVal);
        tvFrom = infoWindowContentsView.findViewById(R.id.customAdapterTvFrom);
        tvTo = infoWindowContentsView.findViewById(R.id.customAdapterTvTo);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        tvCallsign.setText(marker.getTitle());
        tvLat.setText(String.valueOf(marker.getPosition().latitude));
        tvLng.setText(String.valueOf(marker.getPosition().longitude));

        String[] snippet = marker.getSnippet().split(",");
        if (!snippet[0].equals("null")) {
            tvFrom.setText(snippet[0]);
        } else {
            tvFrom.setText("loading..");
            tvFrom.setTextSize(20f);
        }
        if (!snippet[1].equals("null")) {
            tvTo.setText(snippet[1]);
        } else {
            tvTo.setText("loading..");
            tvTo.setTextSize(20f);
        }
        return infoWindowContentsView;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}
