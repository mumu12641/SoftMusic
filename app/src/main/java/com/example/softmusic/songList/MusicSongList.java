package com.example.softmusic.songList;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "musicSongList")
public class MusicSongList {
    @NonNull
    @PrimaryKey
    private String songListTitle;
    @ColumnInfo (name = "createDate")
    private String createDate;
    @ColumnInfo(name = "songNumber")
    private int songNumber;
    @ColumnInfo(name = "builder")
    private String builder;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "imageFileUri")
    private String imageFileUri;

    public MusicSongList(@NonNull String songListTitle, String createDate, int songNumber, String builder,
                         String description, String imageFileUri) {
        this.songListTitle = songListTitle;
        this.createDate = createDate;
        this.songNumber = songNumber;
        this.builder = builder;
        this.description = description;
        this.imageFileUri = imageFileUri;
    }


    public String getSongListTitle() {
        return songListTitle;
    }

    public void setSongListTitle(String songListTitle) {
        this.songListTitle = songListTitle;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getSongNumber() {
        return songNumber;
    }

    public void setSongNumber(int songNumber) {
        this.songNumber = songNumber;
    }

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageFileUri() {
        return imageFileUri;
    }

    public void setImageFileUri(String imageFileUri) {
        this.imageFileUri = imageFileUri;
    }
}
