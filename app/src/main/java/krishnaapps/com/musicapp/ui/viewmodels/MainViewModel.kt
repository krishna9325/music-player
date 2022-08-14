package krishnaapps.com.musicapp.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import krishnaapps.com.musicapp.data.entities.Song
import krishnaapps.com.musicapp.exoplayer.MusicServiceConnectionRepository
import krishnaapps.com.musicapp.exoplayer.isPlayEnabled
import krishnaapps.com.musicapp.exoplayer.isPlaying
import krishnaapps.com.musicapp.exoplayer.isPrepared
import krishnaapps.com.musicapp.other.Constants.MEDIA_ROOT_ID
import krishnaapps.com.musicapp.other.Resource

class MainViewModel @ViewModelInject constructor(
    private val musicServiceConnectionRepository: MusicServiceConnectionRepository
) : ViewModel() {
    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnectionRepository.isConnected
    val networkError = musicServiceConnectionRepository.networkError
    val curPlayingSong = musicServiceConnectionRepository.curPlayingSong
    val playbackState = musicServiceConnectionRepository.playbackState

    init {
        _mediaItems.postValue(Resource.loading(null))
        musicServiceConnectionRepository.subscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    Song(
                        it.mediaId!!,
                        it.description.title.toString(),
                        it.description.subtitle.toString(),
                        it.description.mediaUri.toString(),
                        it.description.iconUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.success(items))
            }
        })
    }

    fun skipToNextSong() {
        musicServiceConnectionRepository.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnectionRepository.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnectionRepository.transportControls.seekTo(pos)
    }

    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if(isPrepared && mediaItem.mediaId ==
            curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if(toggle) musicServiceConnectionRepository.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnectionRepository.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnectionRepository.transportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnectionRepository.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}

















