package com.example.practicapig.ConsumoApis

class ApiResponse {

    var results: List<Character>? = null

    class Character {
        var id: Int = 0
        var name: String? = null
        var gender: String? = null //solo lo uso para buscar en la api pero no me traigo nada ni lo uso, solo filtro
        var image: String? = null
    }
}
