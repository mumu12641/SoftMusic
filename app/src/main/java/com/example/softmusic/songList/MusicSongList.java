package com.example.softmusic.songList;

public class MusicSongList {
    private String songListTitle;
    private String createDate;
    private int songNumber;
    private String builder;

    public MusicSongList(String songListTitle, String createDate, int songNumber, String builder) {
        this.songListTitle = songListTitle;
        this.createDate = createDate;
        this.songNumber = songNumber;
        this.builder = builder;
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
}
