package com.example.practicapig.Chuckstes

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceChuck {

    //devuelve una lista de categorías de chiste, no se usa ni query ni path porque no va entre {} ni detras de ?
    //no hay path ni query porque no hay condiciones siempre devuelve las mismas categorias
     //endpoint: GET /jokes/categories
    @GET("jokes/categories")
    fun getCategorias(): Call<List<String>>


     //devuelve un chiste aleatorio de una categoría concreta.
     //Endpoint: GET /jokes/random?category=animal, por esto se usa query en vez de path, porque va despues de ?

    @GET("jokes/random")
    fun getChistePorCategoria(
        @Query("category") categoria: String
    ): Call<ApiResponseChuck>
}
