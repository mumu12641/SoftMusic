package  com.example.softmusic.network

import com.google.gson.annotations.SerializedName


data class SongDetailMsg (

        @SerializedName("songs"      ) var songs      : ArrayList<Songs>      = arrayListOf(),
        @SerializedName("privileges" ) var privileges : ArrayList<Privileges> = arrayListOf(),
        @SerializedName("code"       ) var code       : Int?                  = null

)
data class Ar (
        @SerializedName("name"  ) val name :String

)
data class Al (

        @SerializedName("picUrl"  ) var picUrl : String?           = null,

)
data class Songs (

        @SerializedName("name"                 ) var name                 : String,
        @SerializedName("id"                   ) var id                   : Int?              = null,
        @SerializedName("ar"                   ) var ar                   : ArrayList<Ar>     = arrayListOf(),
        @SerializedName("fee"                  ) var fee                  : Int?              = null,
        @SerializedName("al"                   ) var al                   : Al?               = Al(),
        @SerializedName("dt"                   ) var dt                   : Int,

)


data class Privileges (

        @SerializedName("fee"                ) var fee                : Int?                      = null,

)