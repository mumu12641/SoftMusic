package com.example.softmusic.musicSong;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.softmusic.songList.MusicSongList;

import java.util.ArrayList;
import java.util.List;

public class MusicSongViewModel extends ViewModel {
    private final MutableLiveData<List<MusicSong>> musicSongData = new MutableLiveData<>();
    private final MutableLiveData<MusicSongList> musicSongListData = new MutableLiveData<>();

    public MutableLiveData<List<MusicSong>> getMusicSongData() {

        List<MusicSong> list = new ArrayList<>();
        list.add(new MusicSong("回到过去","周杰伦","八度空间","none"));
        list.add(new MusicSong("说了再见","周杰伦","跨时代","none"));
        list.add(new MusicSong("枫","周杰伦","11月的萧邦","none"));
        musicSongData.setValue(list);
        return musicSongData;
    }

    public MutableLiveData<MusicSongList> getMusicSongListData() {
        musicSongListData.setValue(new MusicSongList("我喜欢","4/17/22", 50,"me","none","none"));
        return musicSongListData;
    }
}
