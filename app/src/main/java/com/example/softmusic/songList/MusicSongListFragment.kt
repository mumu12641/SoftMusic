package com.example.softmusic.songList

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.MainActivity
import com.example.softmusic.R
import com.example.softmusic.databinding.FragmentSongListBinding
import com.example.softmusic.entity.MusicSongList
import com.example.softmusic.room.DataBaseUtils
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class MusicSongListFragment : Fragment(), View.OnClickListener {
    private lateinit var viewModel: MusicSongListViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentSongListBinding: FragmentSongListBinding =
            FragmentSongListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MusicSongListViewModel::class.java]
        fragmentSongListBinding.songListList.layoutManager =
            GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false)

        val adapter = MusicSongListAdapter(
            requireContext(),
            listOf()
        )
        fragmentSongListBinding.songListList.adapter = adapter
        viewModel.musicSongListLiveData
            .observe(viewLifecycleOwner) { musicSongLists: List<MusicSongList> ->
                if (musicSongLists.isEmpty()) {
                    DataBaseUtils.insertMusicSongList(MusicSongList(
                        0, "我喜欢", "5/3/22", 0, "me", "i like", "like")
                    )
                }
                adapter.setMusicSongListList(musicSongLists)
                (requireActivity()).title = MusicSongListViewModel.title
            }
        fragmentSongListBinding.floatingActionButton.setOnClickListener(this)
        return fragmentSongListBinding.root
    }

    override fun onClick(view: View) {
        val dialog: AlertDialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view1: View = inflater.inflate(R.layout.dialog_add_music_song_list, null, false)
        builder.setView(view1)
        builder.setCancelable(false)
        dialog = builder.create()
        view1.findViewById<View>(R.id.dialog_confirm_sure).setOnClickListener {
            val des = view1.findViewById<TextInputEditText>(R.id.edit_description).text.toString()
            val title: String =
                view1.findViewById<TextInputEditText>(R.id.edit_name).text.toString()
            val calendar = Calendar.getInstance()
            val date = calendar[Calendar.YEAR].toString() +
                    "/" + (calendar[Calendar.MONTH] + 1) +
                    "/" + calendar[Calendar.DAY_OF_MONTH]
            if (des != "" && title != "") {
                DataBaseUtils.insertMusicSongList(
                    MusicSongList(
                        0, title, date, 0, "me", des, "none"
                    )
                )
                dialog.cancel()
            }
        }
        view1.findViewById<View>(R.id.dialog_confirm_cancel)
            .setOnClickListener { dialog.cancel() }
        dialog.show()
    }


}