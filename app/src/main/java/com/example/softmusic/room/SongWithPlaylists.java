package com.example.softmusic.room;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.softmusic.musicSong.MusicSong;
import com.example.softmusic.songList.MusicSongList;

import java.util.List;

public class SongWithPlaylists {
    @Embedded public MusicSong song;
    @Relation(
            parentColumn = "songTitle",
            entityColumn = "songListTitle",
            associateBy = @Junction(PlaylistSongCrossRef.class)
    )
    public List<MusicSongList> playlists;
}
