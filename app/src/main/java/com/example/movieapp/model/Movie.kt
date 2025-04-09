package com.example.movieapp.model

data class Movie(
    var id: String = "",
    var name: String = "",
    var imageUrl: String = "",
    var description: String = "",
    var studio: String = "",
    var rating: Float = 0f,
    var isFavorite: Boolean = false // ✅ This must be here and mutable
) {
    // No-arg constructor required by Firebase
    constructor() : this("", "", "", "", "", 0f, false)
}
