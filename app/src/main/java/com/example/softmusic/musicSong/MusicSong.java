package com.example.softmusic.musicSong;

import com.example.softmusic.songList.MusicSongList;

public class MusicSong {
    private String songTitle;
    private String songSinger;
    private String songAlbum;
    private MusicSongList belong;

    public MusicSong(String songTitle, String songSinger, String songAlbum, MusicSongList belong) {
        this.songTitle = songTitle;
        this.songSinger = songSinger;
        this.songAlbum = songAlbum;
        this.belong = belong;
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

    public MusicSongList getBelong() {
        return belong;
    }

    public void setBelong(MusicSongList belong) {
        this.belong = belong;
    }
}
