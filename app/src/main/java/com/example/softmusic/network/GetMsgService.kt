package com.example.softmusic.network

import retrofit2.http.GET
import retrofit2.http.Query

interface GetMsgService {
    @GET("search")
    suspend fun getSongResultMsg(@Query("keywords") keywords:String,@Query("limit")limit:Int):SongResultMsg
}