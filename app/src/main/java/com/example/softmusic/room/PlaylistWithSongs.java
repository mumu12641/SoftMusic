package com.example.softmusic.room;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.softmusic.musicSong.MusicSong;
import com.example.softmusic.songList.MusicSongList;

import java.util.List;

public class PlaylistWithSongs {
    @Embedded
    public MusicSongList playlist;
    @Relation(
            parentColumn = "songListTitle",
            entityColumn = "songTitle",
            associateBy = @Junction(PlaylistSongCrossRef.class)
    )
    public List<MusicSong> songs;
}
