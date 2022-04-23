package com.example.softmusic.songList;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.softmusic.MainActivity;
import com.example.softmusic.R;
import com.example.softmusic.databinding.FragmentSongListBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class MusicSongListFragment extends Fragment implements View.OnClickListener{

    private MusicSongListViewModel model;

    public MusicSongListFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        com.example.softmusic.databinding.FragmentSongListBinding fragmentSongListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_song_list, container, false);
        model = new ViewModelProvider(requireActivity(),new ViewModelFactory(requireActivity())).get(MusicSongListViewModel.class);
        fragmentSongListBinding.songListList.setLayoutManager(new GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false));
        MusicSongListAdapter adapter = new MusicSongListAdapter(requireContext(),model.getMusicSongListData().getValue());
        fragmentSongListBinding.songListList.setAdapter(adapter);
        model.getMusicSongListLiveData().observe(getViewLifecycleOwner(), musicSongLists -> {
            adapter.setMusicSongListList(musicSongLists);
            ((MainActivity)requireActivity()).setTitle(MusicSongListViewModel.getTitle());
        });
        fragmentSongListBinding.floatingActionButton.setOnClickListener(this);
        return fragmentSongListBinding.getRoot();
    }

    @Override
    public void onClick(View view) {
        AlertDialog dialog;
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.dialog_add_music_song_list,null,false);
        builder.setView(view1);
        builder.setCancelable(false);
        dialog = builder.create();
        AlertDialog finalDialog = dialog;
        view1.findViewById(R.id.dialog_confirm_sure).setOnClickListener(view2 -> {
            String des = Objects.requireNonNull(((TextInputEditText) view1.findViewById(R.id.edit_description)).getText()).toString();
            String title = Objects.requireNonNull(((TextInputEditText) view1.findViewById(R.id.edit_name)).getText()).toString();
            Calendar calendar = Calendar.getInstance();
            String date = calendar.get(Calendar.YEAR) +
                    "/" + (calendar.get(Calendar.MONTH) + 1) +
                    "/" + calendar.get(Calendar.DAY_OF_MONTH) ;
            if (!des.equals("")&&!title.equals("")) {
                model.insertMusicSongList(new MusicSongList(title,date,0,"me",des,"none"));
                finalDialog.cancel();
            }
        });

        view1.findViewById(R.id.dialog_confirm_cancel).setOnClickListener(view22 -> finalDialog.cancel());
        dialog.show();
    }

    static class ViewModelFactory implements ViewModelProvider.Factory{
        private final Context context;

        public ViewModelFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
            return (T)new MusicSongListViewModel(context);
        }
    }

}
