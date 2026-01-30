package com.example.practicapig.Librerias.Videos

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Hub.getParcelableCompat
import com.example.practicapig.databinding.ActivityVideosBinding
import java.io.File

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideosBinding
    private lateinit var adapter: VideoAdapter
    private var usuario: Usuario? = null

    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //--------------------------------toolbar----------------------
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        usuario = intent.getParcelableCompat("usuario")

        // muetsro nombre del usuario en la Toolbar
        binding.nombreUsuario.text = usuario?.nombre ?: "Usuario"
        //------------------------------

        // recicler
        adapter = VideoAdapter { file ->
            reproducirVideo(file)
        }

        binding.recyclerVideos.layoutManager = LinearLayoutManager(this) //pongo un video por fila en vertical(como el profe)
        binding.recyclerVideos.adapter = adapter

        // boton para volver al listado
        binding.btnVolver.setOnClickListener {
            cerrarReproductor()
        }

        cargarVideos()
    }

    private fun cargarVideos() {
        val nombre = usuario?.nombre ?: return //cargo el nombre de usuario

        val carpeta = File(
            getExternalFilesDir(Environment.DIRECTORY_MOVIES), //busco en la ruta de videos
            nombre
        )

        val videos = carpeta
            .listFiles { file -> file.extension.lowercase() == "mp4" }
            ?.toList() ?: emptyList() //cojo todos los videos y muestro la lista de videos

        adapter.setVideos(videos)
    }

    private fun reproducirVideo(file: File) {
        //es la logica detras de el clic en cada video
        // oculto listado y muestro reproductor
        binding.recyclerVideos.visibility = View.GONE
        binding.layoutPlayer.visibility = View.VISIBLE

        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer

        val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    private fun cerrarReproductor() {
        exoPlayer?.release()
        exoPlayer = null

        // vuelvo al listado (visibilidades)
        binding.layoutPlayer.visibility = View.GONE
        binding.recyclerVideos.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
}
