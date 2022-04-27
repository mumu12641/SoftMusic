package com.example.softmusic.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.softmusic.musicSong.MusicSong
import com.example.softmusic.songList.MusicSongList

@Dao
interface MusicDao {
    @Insert
    fun insertMusicSong(vararg songs: MusicSong?)

    @Update
    fun updateMusicSong(vararg songs: MusicSong?)

    @Delete
    fun deleteMusicSong(vararg songs: MusicSong?)

    @get:Query("SELECT * FROM musicSong")
    val allMusicSong: LiveData<List<MusicSong>>

    @Query("SELECT * FROM musicSong WHERE songTitle = :key")
    fun getMusicSongByKey(key:String):MusicSong

    @Insert
    fun insertMusicSongList(vararg songLists: MusicSongList?)

    @Update
    fun updateMusicSongList(vararg songLists: MusicSongList?)

    @Delete
    fun deleteMusicSongList(vararg songLists: MusicSongList?)

    @Insert
    fun insertPlaylistSongCrossRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?)

    @Update
    fun updatePlaylistSongCrossRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?)

    @Delete
    fun deletePlaylistSongCrossRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?)

    @Query("SELECT * FROM playlistsongcrossref WHERE  songListTitle = :songListTitle")
    fun getPlayListSongCrossRefByKey(songListTitle:String):LiveData<List<PlaylistSongCrossRef>>

    @get:Query("SELECT * FROM playlistsongcrossref")
    val allPlayListSongCrossRef:LiveData<List<PlaylistSongCrossRef>>

    @get:Query("SELECT * FROM musicSongList")
    val allMusicSongList: LiveData<List<MusicSongList>>

    @get:Query("SELECT * FROM musicSongList")
    @get:Transaction
    val playlistsWithSongs: List<PlaylistWithSongs?>?

    @get:Query("SELECT * FROM musicSongList")
    @get:Transaction
    val liveDataPlaylistsWithSongs: LiveData<List<PlaylistWithSongs?>?>?

    @Transaction
    @Query("SELECT * FROM musicSongList WHERE songListTitle = :key")
    fun getLiveDataPlaylistsWithSongsByKey(key: String?): LiveData<PlaylistWithSongs>
    @Query("SELECT * FROM musicSongList WHERE songListTitle = :songListTitle")
    fun getPlayListsWithSongsByKey(songListTitle:String):PlaylistWithSongs


    @get:Query("SELECT * FROM musicSong")
    @get:Transaction
    val songsWithPlaylists: List<SongWithPlaylists?>?

    @get:Query("SELECT * FROM musicSong")
    @get:Transaction
    val liveDataSongsWithPlaylists: LiveData<List<SongWithPlaylists?>?>?
}