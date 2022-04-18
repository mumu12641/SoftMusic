package com.example.softmusic.musicSong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softmusic.R;
import com.example.softmusic.databinding.CardSongBinding;
import com.example.softmusic.playMusic.MusicPlayFragment;

import java.util.List;

public class MusicSongAdapter extends RecyclerView.Adapter<MusicSongAdapter.ViewHolder> {
    private final List<MusicSong> musicSongList;
    private final Context context;

    public MusicSongAdapter(Context context,List<MusicSong> musicSongList) {
        this.musicSongList = musicSongList;
        this.context = context;
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
        holder.getCardSongListBinding().songItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController controller = Navigation.findNavController(view);
                controller.navigate(R.id.action_musicSongFragment_to_musicPlayFragment);
            }
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
