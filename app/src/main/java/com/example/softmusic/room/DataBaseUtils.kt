package com.example.softmusic.room

import androidx.lifecycle.LiveData
import com.example.softmusic.musicSong.MusicSong
import com.example.softmusic.songList.MusicSongList

class DataBaseUtils {
    companion object{
        private val dataBase : MusicDataBase = MusicDataBase.getInstance()
        private val musicDao : MusicDao = dataBase.musicDao
//        ExecutorService exec = Executors.newCachedThreadPool();
        fun insertMusicSong(vararg songs:MusicSong) {
            Thread { musicDao.insertMusicSong(*songs) }.start()
        }
        fun updateMusicSong(vararg musicSongs: MusicSong?) {
            Thread { musicDao.updateMusicSong(*musicSongs) }.start()
        }

        fun deleteMusicSong(vararg musicSongs: MusicSong?) {
            Thread { musicDao.deleteMusicSong(*musicSongs) }.start()
        }
        fun insertMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
            Thread { musicDao.insertPlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
        }

        fun deleteMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
            Thread { musicDao.deletePlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
        }

        fun updateMusicSongRef(vararg playlistSongCrossRefs: PlaylistSongCrossRef?) {
            Thread { musicDao.deletePlaylistSongCrossRef(*playlistSongCrossRefs) }.start()
        }
        fun insertMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.insertMusicSongList(*musicSongLists) }.start()
        }

        fun deleteMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.deleteMusicSongList(*musicSongLists) }.start()
        }

        fun updateMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.updateMusicSongList(*musicSongLists) }.start()
        }
        fun getLiveDataPlaylistsWithSongsByKey(key:String):LiveData<PlaylistWithSongs>{
            // TODO with Future
            lateinit var result:LiveData<PlaylistWithSongs>
            val t = Thread {
                result = musicDao.getLiveDataPlaylistsWithSongsByKey(key)
            }
            t.start()
            t.join()
            return result
        }
        fun getAllMusicSongList():LiveData<List<MusicSongList>>{
            return musicDao.allMusicSongList
        }
        fun getMusicSongByKey(key:String):MusicSong{
            lateinit var result:MusicSong
            val t = Thread {
                result = musicDao.getMusicSongByKey(key)
            }
            t.start()
            t.join()
            return result
        }
        fun getPlayListSongCrossRefByKeys(songListTitle:String):LiveData<List<PlaylistSongCrossRef>>{
            lateinit var result:LiveData<List<PlaylistSongCrossRef>>
            val t = Thread {
                result = musicDao.getPlayListSongCrossRefByKey(songListTitle)
            }
            t.start()
            t.join()
            return result
        }
        fun getPlayListsWithSongsByKey(songListTitle:String):List<MusicSong>{
            lateinit var result:PlaylistWithSongs
            val t = Thread {
                result = musicDao.getPlayListsWithSongsByKey(songListTitle)
            }
            t.start()
            t.join()
            return result.songs!!
        }
    }
}