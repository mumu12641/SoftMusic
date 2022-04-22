package com.example.softmusic.musicSong;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "musicSong")
public class MusicSong {
    @NonNull
    @PrimaryKey
    private String songTitle;
    @ColumnInfo(name = "songSinger")
    private String songSinger;
    @ColumnInfo(name = "songAlbum")
    private String songAlbum;
    @ColumnInfo(name = "mediaFileUri")
    private String mediaFileUri;

    public MusicSong(String songTitle, String songSinger, String songAlbum,String mediaFileUri) {
        this.songTitle = songTitle;
        this.songSinger = songSinger;
        this.songAlbum = songAlbum;
        this.mediaFileUri = mediaFileUri;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongSinger() {
        return songSinger;
    }

    public void setSongSinger(String songSinger) {
        this.songSinger = songSinger;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public String getMediaFileUri() {
        return mediaFileUri;
    }

    public void setMediaFileUri(String mediaFileUri) {
        this.mediaFileUri = mediaFileUri;
    }
}
