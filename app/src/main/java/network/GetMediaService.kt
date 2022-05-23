package network

import retrofit2.http.GET
import retrofit2.http.Query

interface GetMediaService {
//    https://netease-cloud-music-api-self-ten.vercel.app/song/url?id=347230
    @GET("song/url")
    suspend fun getSongMediaMsg(@Query("id")id:Int):SongMediaMsg
}