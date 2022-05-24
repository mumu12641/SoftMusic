package network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object NetworkService {
    private const val BASE_URL = "https://netease-cloud-music-api-self-ten.vercel.app/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val getMsgService: GetMsgService = retrofit.create()
    val getMediaService:GetMediaService = retrofit.create()
    val getDetailService:GetDetailService = retrofit.create()
}