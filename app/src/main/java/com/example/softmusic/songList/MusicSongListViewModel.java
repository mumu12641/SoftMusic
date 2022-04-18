package com.example.softmusic.songList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MusicSongListViewModel extends ViewModel {

    private final MutableLiveData<List<MusicSongList>> musicSongListData = new MutableLiveData<>();

    public MutableLiveData<List<MusicSongList>> getMusicSongListData() {
        loadData();
        return musicSongListData;
    }

    private void loadData(){
            List<MusicSongList> list = new ArrayList<>();
            list.add(new MusicSongList("我喜欢","4/17/22", 50,"me"));
            list.add(new MusicSongList("Jay","4/17/22", 60,"me"));
            list.add(new MusicSongList("沈以诚","4/18/22", 10,"me"));
            musicSongListData.setValue(list);
    }
}
