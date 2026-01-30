package com.example.practicapig.Librerias

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import com.example.practicapig.R
import com.example.practicapig.databinding.ActivityMusicaBinding

class MusicaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMusicaBinding
    private lateinit var player: ExoPlayer

    //establezco los valores iniciales del speed, loop y pitch
    private var speed = 1f
    private var pitch = 1f
    private var loop = false

    // Handler para actualizar el progreso
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inicio ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // cojo las canciones, vinculo cada boton con cada cancion
        binding.btnSong1.setOnClickListener { cargarCancion(R.raw.cancion_1) }
        binding.btnSong2.setOnClickListener { cargarCancion(R.raw.cancion_2) }
        binding.btnSong3.setOnClickListener { cargarCancion(R.raw.cancion_3) }

        // boton de Play / Pause
        binding.btnPlayPause.setOnClickListener {
            if (player.isPlaying) player.pause() else player.play()
        }

        // boton de Loop
        binding.btnLoop.setOnClickListener {
            loop = !loop
            player.repeatMode =
                if (loop) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF


            if (loop) {
                binding.btnLoop.alpha = 0.5f // si el boton esta seleccionado, bajo la opacidad
            } else {
                binding.btnLoop.alpha = 1f // si lo desactivo, se vuelve a subir la opacidad
            }
        }

        // boton de velocidad
        binding.btnSpeed.setOnClickListener {
            speed = when (speed) {
                1f -> 1.5f
                1.5f -> 0.5f
                else -> 1f
            }
            player.playbackParameters = PlaybackParameters(speed)
            binding.btnSpeed.text = "Velocidad x$speed"
        }

        // boton de Pitch
        binding.btnPitch.setOnClickListener {
            pitch = when (pitch) {
                1f -> 1.8f  // aumento el pitch a 1.8
                1.8f -> 0.8f  // reduzco el pitch a 0.8
                else -> 1f  // vuelvo al normal
            }
            player.playbackParameters = PlaybackParameters(speed, pitch)  // actualizo el pitch
            binding.btnPitch.text = "Pitch x$pitch"
        }

        // barra de volumen
        binding.seekVolume.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                player.volume = progress / 100f
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // barra de progreso (adelantar / retroceder)
        binding.seekProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && player.duration > 0) {
                    val nuevaPosicion = (player.duration * progress) / binding.seekProgress.max
                    player.seekTo(nuevaPosicion)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Runnable que actualiza el progreso
        updateRunnable = Runnable {
            if (player.isPlaying && player.duration > 0) {
                val progreso =
                    (player.currentPosition * binding.seekProgress.max) / player.duration
                binding.seekProgress.progress = progreso.toInt()
            }
            handler.postDelayed(updateRunnable, 500)
        }
    }

    private fun cargarCancion(resId: Int) {
        val mediaItem = MediaItem.fromUri("android.resource://$packageName/$resId")
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        // reinicio el progreso
        binding.seekProgress.progress = 0
        handler.post(updateRunnable)
    }

    //---------gestiono el ciclo de vida de la activity--------------
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        player.release()
    }

    override fun onPause() {
        super.onPause()
        // Detener la música cuando la app esté en segundo plano
        if (player.isPlaying) {
            player.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        // Reanudar la música si estaba pausada
        if (!player.isPlaying) {
            player.play()
        }
    }
}
