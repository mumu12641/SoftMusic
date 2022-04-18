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
        list.add(new MusicSong("回到过去","周杰伦","八度空间",new MusicSongList("我喜欢","4/17/22", 50,"me")));
        list.add(new MusicSong("说了再见","周杰伦","跨时代",new MusicSongList("我喜欢","4/17/22", 50,"me")));
        list.add(new MusicSong("枫","周杰伦","11月的萧邦",new MusicSongList("我喜欢","4/17/22", 50,"me")));
        musicSongData.setValue(list);
        return musicSongData;
    }

    public MutableLiveData<MusicSongList> getMusicSongListData() {
        musicSongListData.setValue(new MusicSongList("我喜欢","4/17/22", 50,"me"));
        return musicSongListData;
    }
}
