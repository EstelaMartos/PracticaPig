package com.example.practicapig.Librerias.galeria

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.practicapig.BaseDeDatos.Usuario
import com.example.practicapig.Hub.getParcelableCompat
import com.example.practicapig.databinding.ActivityGaleriaBinding
import java.io.File

class GaleriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGaleriaBinding
    private lateinit var adapter: MediaAdapter
    private var usuario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGaleriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // -------------------------TOOLBAR------------------------------------------------
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // recojo usuario del intent
        usuario = intent.getParcelableCompat("usuario")

        // muestro nombre de usuario
        binding.nombreUsuario.text = usuario?.nombre ?: "Usuario"
        //-----------------------------------------------------------------------------------

        // ------------------------------ADAPTER-----------------------------------------
        adapter = MediaAdapter(
            alHacerClick = { file ->  // si el archivo no es un video, al seleccionar llevo a otra activity para verlo
                if (file.extension.lowercase() != "mp4") {
                    val intent = Intent(this, VerFotoActivity::class.java) // defino la clase a la que se va
                    intent.putExtra("ruta", file.absolutePath) // paso la ruta del archivo
                    startActivity(intent) // inicio la nueva actividad
                }
            },
            alCambiarSeleccion = { haySeleccion -> // si hago una selección muestro el botón, sino, no
                binding.btnEliminar.visibility =
                    if (haySeleccion) View.VISIBLE else View.GONE // muestro u oculto el botón de eliminar
            }
        )

        binding.recyclerGaleria.layoutManager = GridLayoutManager(this, 3) // pongo tres elementos por fila
        binding.recyclerGaleria.adapter = adapter

        binding.btnEliminar.setOnClickListener {
            adapter.borrarSeleccionados() // elimino los archivos seleccionados
        }


        cargarArchivos()
    }

    private fun cargarArchivos() {
        val nombre = usuario?.nombre ?: return

        //muestro fotos
        val fotos = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES), //los cojo de la ruta
            nombre
        ).listFiles()?.toList() ?: emptyList()

        //muestro videos
        val videos = File(
            getExternalFilesDir(Environment.DIRECTORY_MOVIES), //los cojo de la ruta
            nombre
        ).listFiles()?.toList() ?: emptyList()

        adapter.setDatos(fotos + videos)
    }
}
