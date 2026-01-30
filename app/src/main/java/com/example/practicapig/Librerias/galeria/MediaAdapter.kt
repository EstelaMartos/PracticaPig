package com.example.practicapig.Librerias.galeria

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practicapig.databinding.ItemMediaBinding
import java.io.File

class MediaAdapter(
    //se ejecuta cuando se hace click sobre un archivo
    private val alHacerClick: (File) -> Unit,

    // avisa a la galeria si hay o no selección
    private val alCambiarSeleccion: (Boolean) -> Unit
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    // lista de archivos que se muestran en la galería (fotos + vídeos)
    private val listaArchivos = mutableListOf<File>()

    // conjunto de archivos seleccionados para borrar
    private val archivosSeleccionados = mutableSetOf<File>()

    inner class MediaViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(file: File) {

            // icono de vídeo
            // si el archivo es un mp4, muestro el icono del triangulo
            binding.iconVideo.visibility =
                if (file.extension.lowercase() == "mp4") View.VISIBLE else View.GONE

            // cargo la imagen o preview de vídeo con Glide
            // Glide carga la miniatura del archivo que sea
            Glide.with(binding.imagen.context)
                .load(file)
                .centerCrop()
                .into(binding.imagen)

            // cuando seleccionamos para borrar
            // se comprueba si he seleccionado el archivo
            val seleccionado = archivosSeleccionados.contains(file)

            // opacidad de la imagen
            // si está seleccionado, bajamos la opacidad
            binding.imagen.alpha = if (seleccionado) 0.5f else 1.0f


            //cuando hago click normal
            binding.root.setOnClickListener {

                if (archivosSeleccionados.isNotEmpty()) { //si la lista de seleccionados para borrar esta llena, se añade
                    cambiarSeleccion(file) //si lo vuelvo a tocar se desselecciona
                } else {
                    alHacerClick(file) //abro la foto o no hago nada si es un video
                }
            }

            // clic largo
            binding.root.setOnLongClickListener {
                // el clock largo siempre activa o desactiva selección
                cambiarSeleccion(file)
                true
            }
        }
    }

    // añade o quita un archivo del conjunto de seleccionados
    private fun cambiarSeleccion(file: File) {
        //logica de si ya esta seleccionado y vuelvo a pulsar se quita
        if (archivosSeleccionados.contains(file)) {
            archivosSeleccionados.remove(file)
        } else {
            archivosSeleccionados.add(file)
        }

        // aviso a la Activity si hay al menos un archivo seleccionado
        alCambiarSeleccion(archivosSeleccionados.isNotEmpty())

        // se refresca la lista para cambiar la opacidad
        notifyDataSetChanged()
    }

    // borra los archivos seleccionados
    fun borrarSeleccionados() {

        // elimino cada archivo del sistema de archivos
        archivosSeleccionados.forEach { it.delete() }

        // los quito de la lista que se muestra
        listaArchivos.removeAll(archivosSeleccionados)

        // limpio la seleccion
        archivosSeleccionados.clear()

        // ya no hay seleccion
        alCambiarSeleccion(false)

        // refresco
        notifyDataSetChanged()
    }

    // establezco los datos iniciales (fotos + vídeos), cargo todos los archivos
    fun setDatos(lista: List<File>) {
        listaArchivos.clear()
        listaArchivos.addAll(lista)

        // reinicio la seleccion
        archivosSeleccionados.clear()
        alCambiarSeleccion(false)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(listaArchivos[position])
    }

    override fun getItemCount(): Int = listaArchivos.size
}
