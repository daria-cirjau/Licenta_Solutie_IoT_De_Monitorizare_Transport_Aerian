package com.licenta.monitorizareavioane.mainpage.userprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.licenta.monitorizareavioane.R;
import com.licenta.monitorizareavioane.login.LogInActivity;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.sync.SyncConfiguration;

public class UserProfileFragment extends Fragment {
    private TextView tvPoints;
    private TextView tvHello;
    private TextView tvMeters;
    private ProgressBar progressBar;
    private ImageView ivCrown;
    private static int noPoints;
    private SyncConfiguration config;

    public UserProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        config = LogInActivity.getConfig();
        init();
        getNoPoints();
        setAllTexts();
    }

    private void setAllTexts() {
        StringBuilder sb = new StringBuilder("So far, you have collected ");
        sb.append(noPoints);
        sb.append(" points. You are now flying at");
        tvPoints.setText(sb);
        sb.setLength(0);
        sb.append(noPoints * 20);
        sb.append(" meters");
        tvMeters.setText(sb);
        progressBar.setProgress(noPoints * 20);
        sb.setLength(0);
        sb.append("Hello, ");
        sb.append(config.getUser().getProfile().getEmail().split("@")[0]);
        tvHello.setText(sb);
        System.out.println(noPoints);
        if (noPoints >= 600) {
            ivCrown.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        tvPoints = getActivity().findViewById(R.id.userProfileFragmentPoints);
        tvMeters = getActivity().findViewById(R.id.userProfileFragmentMeters);
        progressBar = getActivity().findViewById(R.id.progressBar2);
        tvHello = getActivity().findViewById(R.id.userProfileFragmentHelloUser);
        ivCrown = getActivity().findViewById(R.id.imageViewCrown);
    }

    private void getNoPoints() {
        Realm realm = Realm.getInstance(config);
        App app = new App(new AppConfiguration.Builder("applicenta-rkblt")
                .build());
        String currentUserId = app.currentUser().getId();
        Points points = realm.where(Points.class).equalTo("_id", currentUserId).findFirst();
        noPoints = points.getPoints();
    }

    public static int getNoMeters() {
        return noPoints * 20;
    }
}