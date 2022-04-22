package com.example.softmusic.songList;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.softmusic.room.MusicDao;
import com.example.softmusic.room.MusicDataBase;

import java.util.ArrayList;
import java.util.List;

public class MusicSongListViewModel extends ViewModel {

    private final MutableLiveData<List<MusicSongList>> musicSongListData = new MutableLiveData<>();
    private static final String title = "SoftMusic";
    private MusicDao musicDao;
    private LiveData<List<MusicSongList>> musicSongListLiveData;

    public MusicSongListViewModel(Context context) {
        MusicDataBase musicDataBase = MusicDataBase.getInstance(context);
        musicDao = musicDataBase.getMusicDao();
        musicSongListLiveData = musicDao.getAllMusicSongList();
    }

    public LiveData<List<MusicSongList>> getMusicSongListLiveData() {
        return musicSongListLiveData;
    }

    public void insertMusicSongList(MusicSongList...musicSongLists){
        new Thread(() -> musicDao.insertMusicSongList(musicSongLists)).start();
    }
    public void deleteMusicSongList(MusicSongList...musicSongLists){
        new Thread(() -> musicDao.deleteMusicSongList(musicSongLists)).start();
    }
    public void updateMusicSongList(MusicSongList...musicSongLists){
        new Thread(() -> musicDao.updateMusicSongList(musicSongLists)).start();
    }

    public MutableLiveData<List<MusicSongList>> getMusicSongListData() {
        loadData();
        return musicSongListData;
    }

    private void loadData(){
            List<MusicSongList> list = new ArrayList<>();
            list.add(new MusicSongList("我喜欢","4/17/22", 50,"me","none","none"));
            list.add(new MusicSongList("Jay","4/17/22", 60,"me","none","none"));
            list.add(new MusicSongList("沈以诚","4/18/22", 10,"me","none","none"));
            musicSongListData.setValue(list);
    }

    public static String getTitle() {
        return title;
    }
}
