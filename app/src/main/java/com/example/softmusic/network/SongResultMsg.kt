package com.example.softmusic.network

data class SongResultMsg(
    val code: Int,
    val result: Result
)
data class Result(
    val songs: List<Song>
)
data class Song(
    // 歌曲id
    val id: Int,
    val name:String,
    val artists: List<ArtistX>,
)
data class ArtistX(
    val name: String,
)