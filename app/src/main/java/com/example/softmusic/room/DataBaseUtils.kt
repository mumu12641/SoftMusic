package com.example.softmusic.room

import androidx.lifecycle.LiveData
import com.example.softmusic.entity.MusicSong
import com.example.softmusic.entity.MusicSongList
import com.example.softmusic.entity.PlaylistSongCrossRef
import com.example.softmusic.entity.PlaylistWithSongs
import kotlin.properties.Delegates

class DataBaseUtils {
    companion object{
        private val dataBase : MusicDataBase = MusicDataBase.getInstance()
        private val musicDao : MusicDao = dataBase.musicDao
//        ExecutorService exec = Executors.newCachedThreadPool();

        // musicSong
        fun insertMusicSong(song: MusicSong):Long {
            var result by Delegates.notNull<Long>()
            val t = Thread {
                result = musicDao.insertMusicSong(song)
            }
            t.start()
            t.join()
            return result
        }
        fun updateMusicSong(vararg musicSongs: MusicSong?) {
            Thread { musicDao.updateMusicSong(*musicSongs) }.start()
        }
        fun deleteMusicSong(vararg musicSongs: MusicSong?) {
            Thread { musicDao.deleteMusicSong(*musicSongs) }.start()
        }
        fun getAllMusicSongs():List<MusicSong>{
            lateinit var result:List<MusicSong>
            val t = Thread {
                result = musicDao.getAllMusicSongs()
            }
            t.start()
            t.join()
            return result
        }
        fun getMusicSongById(key:Long): MusicSong {
            lateinit var result: MusicSong
            val t = Thread {
                result = musicDao.getMusicSongById(key)
            }
            t.start()
            t.join()
            return result
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

        fun getMusicSongListByKey(key:String): MusicSongList {
            lateinit var result: MusicSongList
            val t = Thread {
                result = musicDao.getMusicSongListByKey(key)
            }
            t.start()
            t.join()
            return result
        }
        fun getMusicSongListById(key:Long): MusicSongList {
            lateinit var result: MusicSongList
            val t = Thread {
                result = musicDao.getMusicSongListById(key)
            }
            t.start()
            t.join()
            return result
        }

        fun updateMusicSongList(vararg musicSongLists: MusicSongList) {
            Thread { musicDao.updateMusicSongList(*musicSongLists) }.start()
        }
        fun getLiveDataPlaylistsWithSongsById(key:Long):LiveData<PlaylistWithSongs>{
            // TODO with Future
            lateinit var result:LiveData<PlaylistWithSongs>
            val t = Thread {
                result = musicDao.getLiveDataPlaylistsWithSongsById(key)
            }
            t.start()
            t.join()
            return result
        }

        fun getAllMusicSongList():LiveData<List<MusicSongList>>{
            return musicDao.allMusicSongList
        }


        fun getPlayListsWithSongsByKey(songListTitle:String):List<MusicSong>{
            lateinit var result: PlaylistWithSongs
            val t = Thread {
                result = musicDao.getPlayListsWithSongsByKey(songListTitle)
            }
            t.start()
            t.join()
            return result.songs
        }
        fun getPlayListsWithSongsById(musicSongListId:Long):List<MusicSong>{
            lateinit var result: PlaylistWithSongs
            val t = Thread {
                result = musicDao.getPlayListsWithSongsById(musicSongListId)
            }
            t.start()
            t.join()
            return result.songs
        }
    }
}