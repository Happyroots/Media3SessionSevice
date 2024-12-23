package com.example.mediasession.ui

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

class PlaybackLibraryService : MediaLibraryService() {
    private var mediaLibrarySession:MediaLibrarySession? = null

    // Create your Player and MediaSession in the onCreate lifecycle event
    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this).build()

    }


    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ) : MediaLibrarySession? = mediaLibrarySession

}