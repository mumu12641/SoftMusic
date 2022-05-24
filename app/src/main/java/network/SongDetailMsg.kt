package network

import com.google.gson.annotations.SerializedName


data class SongDetailMsg (

        @SerializedName("songs"      ) var songs      : ArrayList<Songs>      = arrayListOf(),
        @SerializedName("privileges" ) var privileges : ArrayList<Privileges> = arrayListOf(),
        @SerializedName("code"       ) var code       : Int?                  = null

)


data class Ar (
        @SerializedName("id"    ) var id    : Int?              = null,
        @SerializedName("name"  ) var name  : String?           = null,
        @SerializedName("tns"   ) var tns   : ArrayList<String> = arrayListOf(),
        @SerializedName("alias" ) var alias : ArrayList<String> = arrayListOf()

)
data class Al (

        @SerializedName("id"      ) var id     : Int?              = null,
        @SerializedName("name"    ) var name   : String?           = null,
        @SerializedName("picUrl"  ) var picUrl : String?           = null,
        @SerializedName("tns"     ) var tns    : ArrayList<String> = arrayListOf(),
        @SerializedName("pic_str" ) var picStr : String?           = null

)


data class Songs (

        @SerializedName("name"                 ) var name                 : String?           = null,
        @SerializedName("id"                   ) var id                   : Int?              = null,
        @SerializedName("ar"                   ) var ar                   : ArrayList<Ar>     = arrayListOf(),
        @SerializedName("al"                   ) var al                   : Al?               = Al(),
)


data class ChargeInfoList (

        @SerializedName("rate"          ) var rate          : Int?    = null,
        @SerializedName("chargeUrl"     ) var chargeUrl     : String? = null,
        @SerializedName("chargeMessage" ) var chargeMessage : String? = null,
        @SerializedName("chargeType"    ) var chargeType    : Int?    = null

)

data class Privileges (

        @SerializedName("id"                 ) var id                 : Int?                      = null,
        @SerializedName("fee"                ) var fee                : Int?                      = null,
        @SerializedName("payed"              ) var payed              : Int?                      = null,
        @SerializedName("st"                 ) var st                 : Int?                      = null,
        @SerializedName("pl"                 ) var pl                 : Int?                      = null,
        @SerializedName("dl"                 ) var dl                 : Int?                      = null,
        @SerializedName("sp"                 ) var sp                 : Int?                      = null,
        @SerializedName("cp"                 ) var cp                 : Int?                      = null,
        @SerializedName("subp"               ) var subp               : Int?                      = null,
        @SerializedName("cs"                 ) var cs                 : Boolean?                  = null,
        @SerializedName("maxbr"              ) var maxbr              : Int?                      = null,
        @SerializedName("fl"                 ) var fl                 : Int?                      = null,
        @SerializedName("toast"              ) var toast              : Boolean?                  = null,
        @SerializedName("flag"               ) var flag               : Int?                      = null,
        @SerializedName("preSell"            ) var preSell            : Boolean?                  = null,
        @SerializedName("playMaxbr"          ) var playMaxbr          : Int?                      = null,
        @SerializedName("downloadMaxbr"      ) var downloadMaxbr      : Int?                      = null,
        @SerializedName("maxBrLevel"         ) var maxBrLevel         : String?                   = null,
        @SerializedName("playMaxBrLevel"     ) var playMaxBrLevel     : String?                   = null,
        @SerializedName("downloadMaxBrLevel" ) var downloadMaxBrLevel : String?                   = null,
        @SerializedName("plLevel"            ) var plLevel            : String?                   = null,
        @SerializedName("dlLevel"            ) var dlLevel            : String?                   = null,
        @SerializedName("flLevel"            ) var flLevel            : String?                   = null,
        @SerializedName("rscl"               ) var rscl               : Int?                      = null,
        @SerializedName("chargeInfoList"     ) var chargeInfoList     : ArrayList<ChargeInfoList> = arrayListOf()

)