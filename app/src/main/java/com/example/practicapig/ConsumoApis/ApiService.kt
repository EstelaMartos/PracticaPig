package com.example.practicapig.ConsumoApis

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    //vuelve a ir detras de ? por lo tanto es query no path
    //endpoint GET api/character/?name=rick&status=alive
    @GET("api/character/")
    fun getCharactersByGender(
        @Query("gender") gender: String): Call<ApiResponse>
}
