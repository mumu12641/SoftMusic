package com.example.softmusic.musicSong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softmusic.R;
import com.example.softmusic.databinding.CardSongBinding;
import com.example.softmusic.interfaceListener.ChangeFragmentListener;
import com.example.softmusic.playMusic.MusicPlayFragment;

import java.util.List;

public class MusicSongAdapter extends RecyclerView.Adapter<MusicSongAdapter.ViewHolder> {
    private final List<MusicSong> musicSongList;
    private final Context context;
    private final ChangeFragmentListener listener;

    public MusicSongAdapter(Context context,List<MusicSong> musicSongList,ChangeFragmentListener listener) {
        this.musicSongList = musicSongList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardSongBinding cardSongListBinding = DataBindingUtil.inflate(
                (LayoutInflater.from(context)), R.layout.card_song,parent,false);
        return new MusicSongAdapter.ViewHolder(cardSongListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getCardSongListBinding().setTheSong(musicSongList.get(position));
        holder.getCardSongListBinding().number.setText(String.valueOf(position + 1));
        holder.getCardSongListBinding().songItem.setOnClickListener(view -> {
            MusicPlayFragment fragment = new MusicPlayFragment(context);
            listener.changeFragment(fragment);
        });
    }



    @Override
    public int getItemCount() {
        return musicSongList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardSongBinding cardSongListBinding;
        public ViewHolder(@NonNull CardSongBinding cardSongListBinding) {
            super(cardSongListBinding.getRoot());
            this.cardSongListBinding = cardSongListBinding;
        }

        public CardSongBinding getCardSongListBinding() {
            return cardSongListBinding;
        }
    }
}
