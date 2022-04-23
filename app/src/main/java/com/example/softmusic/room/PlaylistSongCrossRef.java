package com.example.softmusic.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"songListTitle", "songTitle"},tableName = "PlaylistSongCrossRef")
public class PlaylistSongCrossRef {
    @NonNull
    public String songListTitle;
    @NonNull
    public String songTitle;

    public PlaylistSongCrossRef(@NonNull String songListTitle, @NonNull String songTitle) {
        this.songListTitle = songListTitle;
        this.songTitle = songTitle;
    }
}
