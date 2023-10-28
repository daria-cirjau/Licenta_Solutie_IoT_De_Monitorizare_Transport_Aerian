package com.licenta.monitorizareavioane.mainpage.userprofile;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.RealmClass;

@RealmClass(name = "points")
public class Points extends RealmObject {
    @io.realm.annotations.PrimaryKey
    @NotNull
    @Index
    private String _id;
    private int points;

    public Points() {
    }

    public Points(@NotNull String _id, int points) {
        this._id = _id;
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

}
