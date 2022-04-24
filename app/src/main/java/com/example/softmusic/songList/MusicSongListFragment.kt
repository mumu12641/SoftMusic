package com.example.softmusic.songList

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.softmusic.R
import com.example.softmusic.databinding.FragmentSongListBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class MusicSongListFragment : Fragment(), View.OnClickListener {
    private lateinit var viewModel: MusicSongListViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentSongListBinding: FragmentSongListBinding =
            FragmentSongListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            requireActivity()
        ).get(
            MusicSongListViewModel::class.java
        )
        fragmentSongListBinding.songListList.layoutManager =
            GridLayoutManager(requireActivity(), 1, GridLayoutManager.VERTICAL, false)
        val adapter = MusicSongListAdapter(
            requireContext(),
            viewModel.getMusicSongListData().value!!
        )
        fragmentSongListBinding.songListList.adapter = adapter
        viewModel.musicSongListLiveData
            .observe(viewLifecycleOwner) { musicSongLists: List<MusicSongList> ->
                adapter.setMusicSongListList(musicSongLists)
                (requireActivity()).title = MusicSongListViewModel.title
            }
        fragmentSongListBinding.floatingActionButton.setOnClickListener(this)
        return fragmentSongListBinding.root
    }

    override fun onClick(view: View) {
        val dialog: AlertDialog
        val builder: AlertDialog.Builder
        builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val view1: View = inflater.inflate(R.layout.dialog_add_music_song_list, null, false)
        builder.setView(view1)
        builder.setCancelable(false)
        dialog = builder.create()
        view1.findViewById<View>(R.id.dialog_confirm_sure).setOnClickListener { view2: View? ->
//            val des: String =
//                Objects.requireNonNull<Editable>((view1.findViewById<View>(R.id.edit_description) as TextInputEditText).text)
//                    .toString()
            val des = view1.findViewById<TextInputEditText>(R.id.edit_description).text.toString()
            val title: String =
                view1.findViewById<TextInputEditText>(R.id.edit_name).text.toString()

            val calendar = Calendar.getInstance()
            val date = calendar[Calendar.YEAR].toString() +
                    "/" + (calendar[Calendar.MONTH] + 1) +
                    "/" + calendar[Calendar.DAY_OF_MONTH]
            if (des != "" && title != "") {
                viewModel.insertMusicSongList(MusicSongList(title, date, 0, "me", des, "none"))
                dialog.cancel()
            }
        }
        view1.findViewById<View>(R.id.dialog_confirm_cancel)
            .setOnClickListener { view22: View? -> dialog.cancel() }
        dialog.show()
    }

}