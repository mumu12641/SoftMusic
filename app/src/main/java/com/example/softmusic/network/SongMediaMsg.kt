package com.example.softmusic.network

data class SongMediaMsg(
    val code: Int,
    val data: List<Data>
)

data class Data(
    // uri
    val url: String?,
)