package com.licenta.monitorizareavioane.mainpage.recyclerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.licenta.monitorizareavioane.R;
import com.licenta.monitorizareavioane.favoritesdb.Favorites;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private List<Favorites> favoritesList;
    private Context context;
    private int layout;

    public FavoritesAdapter(List<Favorites> favoritesList, Context context, int layout) {
        this.favoritesList = favoritesList;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_layout, parent, false);
        return new FavoritesViewHolder(itemView);
    }


    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        Favorites currentFavorite = favoritesList.get(position);
        if (currentFavorite.getPlanePictureData() != null) {
            holder.getImageViewPlanePicture().setImageBitmap(stringToBitMap(currentFavorite.getPlanePictureData()));
        }

        holder.getTextViewPlaneNumber().setText(currentFavorite.getFlightNumber());

        StringBuilder stringBuilderDate = new StringBuilder();
        stringBuilderDate.append(currentFavorite.getDepartureTime());
        stringBuilderDate.append("->");
        stringBuilderDate.append(currentFavorite.getArrivalTime());
        holder.getTextViewLatestFlightDate().setText(stringBuilderDate.toString());

        StringBuilder stringBuilderLocation = new StringBuilder();
        stringBuilderLocation.append(currentFavorite.getDepartureLocation());
        stringBuilderLocation.append("->");
        stringBuilderLocation.append(currentFavorite.getArrivalLocation());
        holder.getTextViewLatestFlightLoocation().setText(stringBuilderLocation.toString());
    }

    @Override
    public int getItemCount() {
        if (favoritesList != null)
            return favoritesList.size();
        else return 0;
    }

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public List<Favorites> getFavoritesList() {
        return favoritesList;
    }
}
