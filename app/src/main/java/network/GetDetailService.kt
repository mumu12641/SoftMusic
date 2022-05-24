package network

import retrofit2.http.GET
import retrofit2.http.Query

interface GetDetailService {
    @GET("song/detail")
    suspend fun getSongDetailMsg(@Query("ids")ids:Int):SongDetailMsg
}