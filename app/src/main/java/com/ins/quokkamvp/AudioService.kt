package com.ins.quokkamvp

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class AudioService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        val requestId = intent?.getStringExtra("request_id").orEmpty()
        Log.d("Hinaka", "requestId = $requestId")

        val url = Locations.firstOrNull { it.id == requestId }?.url
        if (url != null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepare() // might take long! (for buffering, etc)
                start()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        // Do something when the audio is prepared
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        // Do something when the audio playback is complete
    }
}
