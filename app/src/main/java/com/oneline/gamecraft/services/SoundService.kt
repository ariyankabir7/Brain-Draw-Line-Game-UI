package com.oneline.gamecraft.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.oneline.gamecraft.R

class SoundService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Initialize and start the media player
        mediaPlayer = MediaPlayer.create(this, R.raw.bg_music)
        mediaPlayer.isLooping = true // Loop the music
        mediaPlayer.start()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop and release the media player
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}