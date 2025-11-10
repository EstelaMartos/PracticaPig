package com.example.practicapig.JuegoPig

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practicapig.databinding.ItemNombreBinding

class NombreAdapter(private val activity: SegundaActivity, // llamo a la Activity cuando se pulsa un nombre
    private val indiceJugador: Int         // identifico a qué jugador pertenece cada RecyclerView
) : RecyclerView.Adapter<NombreAdapter.NombreViewHolder>() {

    // hago la lista de nombres que se muestran al inicio
    private var nombresVisibles: List<String> = emptyList()

    // nombre seleccionado por el jugador, hago esto para marcarlos en negrita despues
    private var nombreSeleccionadoActual: String? = null


    // el ViewHolder es cada contenedor de un nombre
    inner class NombreViewHolder(private val binding: ItemNombreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // pinta los nombres en la vista
        fun bind(nombre: String) {
            binding.textViewNombre.text = nombre

            // marco en negrita el nomre seleccionado
            if (nombreSeleccionadoActual != null && nombreSeleccionadoActual == nombre) {
                binding.textViewNombre.setTypeface(null, Typeface.BOLD)
            } else {
                binding.textViewNombre.setTypeface(null, Typeface.NORMAL)
            }

            // controlo lo que sucede cuando se hace click en un nombre
            binding.root.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    activity.gestionarSeleccionDeNombre(nombre, indiceJugador)
                }
            })
        }
    }


    // ----------------creao la vista del item nombre
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NombreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNombreBinding.inflate(inflater, parent, false)
        return NombreViewHolder(binding)
    }


    // -------------------asigno los datos a la vista
    override fun onBindViewHolder(holder: NombreViewHolder, position: Int) {
        val nombre = nombresVisibles[position]
        holder.bind(nombre)
    }


    //--------- cuento cuantos elementos hay
    override fun getItemCount(): Int = nombresVisibles.size


    //------------- actualizo la lista que se muestra y la selección
    fun actualizarDatos(nuevaLista: List<String>, seleccionActual: String?) {
        nombresVisibles = nuevaLista
        nombreSeleccionadoActual = seleccionActual
        notifyDataSetChanged()
    }
}
