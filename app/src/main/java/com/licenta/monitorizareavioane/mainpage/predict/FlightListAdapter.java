package com.licenta.monitorizareavioane.mainpage.predict;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.licenta.monitorizareavioane.R;

import java.util.ArrayList;
import java.util.List;

public class FlightListAdapter extends BaseAdapter {
    private List<FlightPrediction> flightPredictions = new ArrayList<>();
    private Context context;

    public FlightListAdapter(List<FlightPrediction> flightPredictions, Context context) {
        this.flightPredictions = flightPredictions;
        this.context = context;
    }

    @Override
    public int getCount() {
        return flightPredictions.size();
    }

    @Override
    public Object getItem(int i) {
        if (i < flightPredictions.size()) {
            return flightPredictions.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View returnedView = layoutInflater.inflate(R.layout.list_item_custom, viewGroup, false);
        FlightPrediction flightPrediction = (FlightPrediction) flightPredictions.get(i);

        TextView tvFrom = returnedView.findViewById(R.id.customListTvFromRegion);
        TextView tvTo = returnedView.findViewById(R.id.customListTvToRegion);
        TextView tvIata = returnedView.findViewById(R.id.customListTvIataCodeAirlineValue);
        TextView tvScheduledDep = returnedView.findViewById(R.id.customListTvScheduledDepTimeValue);
        TextView tvScheduledArr = returnedView.findViewById(R.id.customListTvScheduledArrTimeValue);
        TextView tvPred = returnedView.findViewById(R.id.customListTvPredictedDelayValue);

        tvFrom.setText(flightPrediction.getOrigin());
        tvTo.setText(flightPrediction.getArrival());
        tvIata.setText(flightPrediction.getAirline());
        String formattedDepTime = String.format("%02d:%02d", (int) flightPrediction.getDepTime() / 100, (int) flightPrediction.getDepTime() % 100);
        tvScheduledDep.setText(formattedDepTime);
        String formattedArrTime = String.format("%02d:%02d", (int) flightPrediction.getArrTime() / 100, (int) flightPrediction.getArrTime() % 100);
        tvScheduledArr.setText(formattedArrTime);

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%.3f", flightPrediction.getPredictedDelay()));
        sb.append(" seconds (");
        sb.append(String.format("%.5f", flightPrediction.getPredictedDelay() / 60.0));
        sb.append(" minutes).");
        if (Math.abs(flightPrediction.getPredictedDelay() / 60.0) < 10) {
            sb.append(" \n Flight will not be considered delayed.");
        } else {
            sb.append(" \n Flight will be considered delayed.");
        }
        tvPred.setText(sb);

        return returnedView;
    }
}
