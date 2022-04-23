package com.example.softmusic.musicSong;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.softmusic.room.MusicDao;
import com.example.softmusic.room.MusicDataBase;
import com.example.softmusic.room.PlaylistSongCrossRef;
import com.example.softmusic.room.PlaylistWithSongs;
import com.example.softmusic.songList.MusicSongList;

import java.util.List;

public class MusicSongViewModel extends ViewModel {

    private static final String TAG = "MusicSongViewModel";
    
    private final MutableLiveData<List<MusicSong>> musicSongData = new MutableLiveData<>();
    private final MutableLiveData<MusicSongList> musicSongListData = new MutableLiveData<>();
    private final LiveData<PlaylistWithSongs> playlistWithSongsData;
    private MusicDao musicDao;
    private String key;

    private LiveData<List<MusicSong>> test = new MutableLiveData<>();
    private final MutableLiveData<MusicSongList> testList = new MutableLiveData<>();


    public MusicSongViewModel(Context context, String musicSongListTitle) {
        key = musicSongListTitle;
        MusicDataBase musicDataBase = MusicDataBase.getInstance(context);
        musicDao = musicDataBase.getMusicDao();
        playlistWithSongsData = musicDao.getLiveDataPlayListWithKey(musicSongListTitle);

        test = musicDao.getAllMusicSong();
    }

//    public MutableLiveData<List<MusicSong>> getMusicSongData() {
//        musicSongData.setValue(Objects.requireNonNull(playlistWithSongsData.getValue()).songs);
//        return musicSongData;
//    }
//
//    public MutableLiveData<MusicSongList> getMusicSongListData() {
//        musicSongListData.setValue(Objects.requireNonNull(playlistWithSongsData.getValue()).playlist);
//        return musicSongListData;
//    }

    public void insertMusicSongRef(PlaylistSongCrossRef...playlistSongCrossRefs){
        new Thread(() -> musicDao.insertPlaylistSongCrossRef(playlistSongCrossRefs)).start();
    }
    public void deleteMusicSongRef(PlaylistSongCrossRef...playlistSongCrossRefs){
        new Thread(() -> musicDao.deletePlaylistSongCrossRef(playlistSongCrossRefs)).start();
    }
    public void updateMusicSongRef(PlaylistSongCrossRef...playlistSongCrossRefs){
        new Thread(() -> musicDao.deletePlaylistSongCrossRef(playlistSongCrossRefs)).start();
    }

    public void insertMusicSong(MusicSong...musicSongs){
        new Thread(() -> musicDao.insertMusicSong(musicSongs)).start();
    }
    public void updateMusicSong(MusicSong...musicSongs){
        new Thread(() -> musicDao.updateMusicSong(musicSongs)).start();
    }
    public void deleteMusicSong(MusicSong...musicSongs){
        new Thread(() -> musicDao.deleteMusicSong(musicSongs)).start();
    }

    public LiveData<List<MusicSong>> getTest() {
        return test;
    }

    public MutableLiveData<MusicSongList> getTestList() {
        testList.setValue(new MusicSongList("我喜欢","4/17/22", 50,"me","none","none"));
        return testList;
    }
}
