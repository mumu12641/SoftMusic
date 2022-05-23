package network

data class SongResultMsg(
    val code: Int,
    val result: Result
)

data class Result(
    val hasMore: Boolean,
    val songCount: Int,
    val songs: List<Song>
)

data class Song(
    val album: Album,
    val alias: List<String>,
    val artists: List<ArtistX>,
    val copyrightId: Int,

    //duration
    val duration: Int,

    val fee: Int,
    val ftype: Int,

    // 歌曲id
    val id: Int,

    val mvid: Int,

    // name
    val name: String,

    val rUrl: Any,
    val rtype: Int,
    val status: Int,
    val transNames: List<String>
)

data class Album(
    val alia: List<String>,
    val artist: Artist,
    val copyrightId: Int,
    val id: Int,
    val mark: Int,
    val name: String,
    val picId: Long,
    val publishTime: Long,
    val size: Int,
    val status: Int
)

data class ArtistX(
    val albumSize: Int,
    val alias: List<Any>,
    val id: Int,
    val img1v1: Int,
    val img1v1Url: String,
    val name: String,
    val picId: Int,
    val picUrl: Any,
    val trans: Any
)

data class Artist(
    val albumSize: Int,
    val alias: List<Any>,
    val id: Int,
    val img1v1: Int,
    val img1v1Url: String,
    val name: String,
    val picId: Int,
    val picUrl: Any,
    val trans: Any
)