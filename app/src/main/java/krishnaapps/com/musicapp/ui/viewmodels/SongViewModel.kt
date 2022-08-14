package krishnaapps.com.musicapp.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import krishnaapps.com.musicapp.exoplayer.MusicService
import krishnaapps.com.musicapp.exoplayer.MusicServiceConnectionRepository
import krishnaapps.com.musicapp.exoplayer.currentPlaybackPosition
import krishnaapps.com.musicapp.other.Constants.UPDATE_PLAYER_POSITION_INTERVAL

class SongViewModel @ViewModelInject constructor(
    musicServiceConnectionRepository: MusicServiceConnectionRepository
) : ViewModel() {

    private val playbackState = musicServiceConnectionRepository.playbackState

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    init {
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while(true) {
                val pos = playbackState.value?.currentPlaybackPosition
                if(curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }
}



















