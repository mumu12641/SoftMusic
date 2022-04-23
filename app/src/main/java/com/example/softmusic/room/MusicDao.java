package com.example.softmusic.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.softmusic.musicSong.MusicSong;
import com.example.softmusic.songList.MusicSongList;

import java.util.List;

@Dao
public interface MusicDao {
    @Insert
    void insertMusicSong(MusicSong...songs);
    @Update
    void updateMusicSong(MusicSong...songs);
    @Delete
    void deleteMusicSong(MusicSong...songs);
    @Query("SELECT * FROM musicSong")
    LiveData<List<MusicSong>> getAllMusicSong();

    @Insert
    void insertMusicSongList(MusicSongList...songLists);
    @Update
    void updateMusicSongList(MusicSongList...songLists);
    @Delete
    void deleteMusicSongList(MusicSongList...songLists);

    @Insert
    void insertPlaylistSongCrossRef(PlaylistSongCrossRef...playlistSongCrossRefs);
    @Update
    void updatePlaylistSongCrossRef(PlaylistSongCrossRef...playlistSongCrossRefs);
    @Delete
    void deletePlaylistSongCrossRef(PlaylistSongCrossRef...playlistSongCrossRefs);


    @Query("SELECT * FROM musicSongList")
    LiveData<List<MusicSongList>> getAllMusicSongList();

    @Transaction
    @Query("SELECT * FROM musicSongList")
    public List<PlaylistWithSongs> getPlaylistsWithSongs();

    @Transaction
    @Query("SELECT * FROM musicSongList")
    public LiveData<List<PlaylistWithSongs>> getLiveDataPlaylistsWithSongs();

    @Transaction
    @Query("SELECT * FROM musicSongList WHERE songListTitle = :key")
    public LiveData<PlaylistWithSongs> getLiveDataPlayListWithKey(String key);

    @Transaction
    @Query("SELECT * FROM musicSong")
    public List<SongWithPlaylists> getSongsWithPlaylists();

    @Transaction
    @Query("SELECT * FROM musicSong")
    public LiveData<List<SongWithPlaylists>> getLiveDataSongsWithPlaylists();

}
