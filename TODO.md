# 新人任务

搭好大概的activity color toobar 和 fragment   	差不多完成

这周完成一下MVVM相关搭建

Material Design 3

Adaptive Color

Navigation 

ExoPlayer



## 4/19/22

开源icon

模板fragment

toolbar & card

test ExoPlayer



昨天的还没push

## 4/20/22

ExoPlayer MediaSession



## 4/22/22

navigation  done

wip room

seekbar		 // duration and currentPosition // getPosition???? // 有待改进



## 4/23/22

room

get local download music

add musicSongList dialog





Database 封装 Dao 			done

将可空类型转成不可空类型 			done

databinding 				done

toolbar的title				done

//考虑可以在application中直接缓存数据list 

//考虑不使用数据flow 和LiveData

添加歌曲   done

​	

歌曲的文件路径	done



添加歌曲的时候判断是否已经存在这个歌曲		done

在MediaplayBackService中的歌曲的列表播放		done



直接点击PlayFragment闪退 		done





## 5/1/22

data class		done

huawei storage		done

要保留上一次播放的状态		done 还有一点小bug

mediaPalyer换成exoPlayer（因为duration的问题已经解决）	wip 感觉更麻烦？？



PermissionX		done

PlayFragment 中的viewmodel 以及 显示进度条时间  wip 效果不好？



**database修改 主键改成id**  		done

notification

fragment and navigation bottom bar		done

分包		done

**应该在activity中bind到这个service，然后在viewmodel可以存现在播放的歌曲信息	回调 brower等等	**		done



首先 service 中应该是上次播放的记录信息 ，通过这个信息loadChildren给activity以及viewmodel 如果我们直接点击play的fragment，应该从Viewmodel中最起码获得歌曲的title 现在的pos，然后在我们的fragment中间			done



如何在最开始就本地音乐插入进去 成为默认的音乐库		done

service中的判空			done



## 5/3/22

接下来UI美化

通知栏前台服务

DataBaseUtil中的join改成future或者协程		done



## 5/6/22

我喜欢	done

播放列表更新 done

播放顺序更新 done



代码优化

PagerSnapHelper	done

## 5/8/22

更改优化一下代码架构

![github](/home/pb/AndroidStudioProjects/Code/SoftMusic/github.png)

使用一个MusicServiceConnection，在这个里面我们应该observe到service的状态

当我们的view中按下暂停键的时候，我们应该用controller更改service的状态，与此同时view在observe的viewmodel的connection更改，从而通知更改UI

```kotlin
package com.example.softmusic

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData

/**
 * 用于连接到service，并且在这个里面observe到service的状态
 */
class MusicServiceConnection(context: Context,serviceComponent:ComponentName) {
    val isConnected = MutableLiveData<Boolean>().apply {
        postValue(false)
    }

    // now playState
    val playbackState = MutableLiveData<PlaybackStateCompat>().apply {
        postValue(EMPTY_PLAYBACK_STATE)
    }

    // Now Metadata
    val nowPlaying = MutableLiveData<MediaMetadataCompat>().apply {
        postValue(NOTHING_PLAYING)
    }

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private lateinit var mediaController:MediaControllerCompat
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply { connect() }

    val rootMediaId : String get() = mediaBrowser.root

    private inner class MediaBrowserConnectionCallback(private val context: Context):
            MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()
            // get Controller
            mediaController = MediaControllerCompat(context,mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            isConnected.postValue(true)
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback :MediaControllerCompat.Callback(){
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            nowPlaying.postValue(metadata)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }


    companion object {
        // For Singleton instantiation.
        @Volatile
        private var instance: MusicServiceConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: MusicServiceConnection(context, serviceComponent)
                    .also { instance = it }
            }
    }

    @Suppress("PropertyName")
    val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
        .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
        .build()

    @Suppress("PropertyName")
    val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
        .build()
}
```

