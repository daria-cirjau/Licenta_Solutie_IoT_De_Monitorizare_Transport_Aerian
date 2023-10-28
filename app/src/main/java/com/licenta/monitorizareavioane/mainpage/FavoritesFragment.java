package com.licenta.monitorizareavioane.mainpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.monitorizareavioane.R;
import com.licenta.monitorizareavioane.favoritesdb.Favorites;
import com.licenta.monitorizareavioane.login.LogInActivity;
import com.licenta.monitorizareavioane.mainpage.recyclerview.FavoritesAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerViewFavorites;
    //    private FavoritesDatabase favoritesDb;
    private App app;
    private String currentUserId;

    public FavoritesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = new App(new AppConfiguration.Builder("applicenta-rkblt")
                .build());

        currentUserId = app.currentUser().getId();
        init();
    }

    private void init() {
        recyclerViewFavorites = getActivity().findViewById(R.id.favFragmentRecyclerView);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(getFavoritesList()
                , getContext(), R.layout.recycler_view_item_layout);
        recyclerViewFavorites.setAdapter(favoritesAdapter);
    }

    private List<Favorites> getFavoritesList() {
        Realm realm = Realm.getInstance(LogInActivity.getConfig());
        List<Favorites> favorites = realm.where(Favorites.class).equalTo("userId", currentUserId).findAll();
        return favorites;
    }

}


