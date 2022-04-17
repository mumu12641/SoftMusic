package com.example.softmusic.songList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softmusic.interfaceListener.ChangeFragmentListener;
import com.example.softmusic.databinding.CardSongListBinding;
import com.example.softmusic.musicSong.MusicSongFragment;
import com.example.softmusic.R;

import java.util.List;

public class MusicSongListAdapter extends RecyclerView.Adapter<MusicSongListAdapter.ViewHolder> {

    private final List<MusicSongList> musicSongListList;
    private final Context context;
    private final ChangeFragmentListener lister;

    public MusicSongListAdapter(Context context, List<MusicSongList> musicSongListList, ChangeFragmentListener lister) {
        this.musicSongListList = musicSongListList;
        this.context = context;
        this.lister = lister;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardSongListBinding cardSongListBinding = DataBindingUtil.inflate(
                (LayoutInflater.from(context)), R.layout.card_song_list,parent,false);
        return new ViewHolder(cardSongListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getCardSongListBinding().setMusicListItem(musicSongListList.get(position));
        holder.getCardSongListBinding().songListItem.setOnClickListener(view -> {
            MusicSongFragment musicSongFragment = new MusicSongFragment(context,musicSongListList.get(position));
            lister.changeFragment(musicSongFragment);
        });
    }

    @Override
    public int getItemCount() {
        return musicSongListList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardSongListBinding cardSongListBinding;
        public ViewHolder(@NonNull CardSongListBinding cardSongListBinding) {
            super(cardSongListBinding.getRoot());
            this.cardSongListBinding = cardSongListBinding;
        }

        public CardSongListBinding getCardSongListBinding() {
            return cardSongListBinding;
        }
    }
}
