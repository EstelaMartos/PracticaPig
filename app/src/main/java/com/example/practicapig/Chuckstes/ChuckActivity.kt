package com.example.practicapig.Chuckstes

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.practicapig.databinding.ActivityChuckBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class ChuckActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChuckBinding
    private var categorias: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChuckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ---------- el retrofit ----------
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.chucknorris.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiServiceChuck::class.java)

        // ---------- cargo las categorias de la api ----------
        val cargarCategorias = service.getCategorias()

        cargarCategorias.enqueue(object : Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    categorias = response.body()!!

                    val listaSpinner = mutableListOf("Selecciona categoría")
                    listaSpinner.addAll(categorias)

                    val adapter = ArrayAdapter(
                        this@ChuckActivity,
                        android.R.layout.simple_spinner_item,
                        listaSpinner
                    )
                    adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                    )
                    binding.spinnerCategorias.adapter = adapter

                    // spiner de categorias
                    binding.spinnerCategorias.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: android.view.View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position != 0) {

                                    val categoriaSeleccionada = listaSpinner[position]
                                    cargarChiste(service, categoriaSeleccionada)

                                    // vuelvo a poner 0 para detectar cambios
                                    binding.spinnerCategorias.setSelection(0)
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                }
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                binding.textChiste.text = "Error al cargar categorías"
                Log.d("Error", "error api")
            }
        })
    }

    // --------------------------------------cargo el chiste-----------------------------------
    private fun cargarChiste(service: ApiServiceChuck, categoria: String)
    {

        val callChiste = service.getChistePorCategoria(categoria)

        callChiste.enqueue(object : Callback<ApiResponseChuck> {
            override fun onResponse(
                call: Call<ApiResponseChuck>,
                response: Response<ApiResponseChuck>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    binding.textChiste.text = response.body()!!.value
                }
            }

            override fun onFailure(call: Call<ApiResponseChuck>, t: Throwable) {
                binding.textChiste.text = "Error al cargar el chiste"
                Log.d("Estela", "errooooooor")
            }
        })
    }


}
