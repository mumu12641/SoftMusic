package com.example.softmusic.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.softmusic.musicSong.MusicSong;
import com.example.softmusic.songList.MusicSongList;

@Database(entities = {MusicSongList.class, MusicSong.class,PlaylistSongCrossRef.class},version = 1,exportSchema = false)
public abstract class MusicDataBase extends RoomDatabase {
    private static MusicDataBase musicDataBase;

    public MusicDataBase() {

    }
    synchronized public static MusicDataBase getInstance(Context context) {
        if (musicDataBase == null){
            musicDataBase = Room.databaseBuilder(context,MusicDataBase.class,"music").build();
        }
        return musicDataBase;
    }

    public abstract MusicDao getMusicDao();
}
