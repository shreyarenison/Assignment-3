package com.example.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.example.movieapp.model.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val repo = MovieRepository()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    init {
        viewModelScope.launch {
            repo.getMovies().collect { movieList ->
                _movies.value = movieList
            }
        }

        viewModelScope.launch {
            repo.migrateIsFavoriteFieldIfMissing()
        }
    }

    fun addMovie(movie: Movie) = viewModelScope.launch {
        repo.addMovie(movie)
    }

    fun updateMovie(movie: Movie) = viewModelScope.launch {
        repo.updateMovie(movie)
    }

    fun deleteMovie(movieId: String) = viewModelScope.launch {
        repo.deleteMovie(movieId)
    }

    fun updateDescription(movieId: String, newDesc: String) = viewModelScope.launch {
        repo.updateMovieDescription(movieId, newDesc)
    }

    fun toggleFavoriteWithUndo(movie: Movie, onUndoRequested: (Movie) -> Unit) =
        viewModelScope.launch {
            val newFavorite = !movie.isFavorite
            repo.updateFavoriteStatus(movie.id, newFavorite)
            if (!newFavorite) {
                onUndoRequested(movie.copy(isFavorite = true))
            }
        }


    fun seedSampleMoviesIfNeeded() = viewModelScope.launch {
        if (!repo.hasSeededMovies()) {
            val sampleMovies = listOf(
                Movie(
                    name = "Parasite",
                    studio = "CJ Entertainment",
                    rating = 8.6f,
                    description = "A cunning tale of greed and class conflict as a poor family infiltrates a wealthy household.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/9Ig5bBlsJ9rmfYtLuJ2pKj9MHB.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "Joker",
                    studio = "Warner Bros.",
                    rating = 8.5f,
                    description = "An origin story exploring the descent into madness of a troubled comedian in Gotham City.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "Avengers: Infinity War",
                    studio = "Marvel Studios",
                    rating = 8.4f,
                    description = "An epic crossover where heroes unite to confront the formidable threat of Thanos.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "Tenet",
                    studio = "Warner Bros.",
                    rating = 7.8f,
                    description = "A mind-bending espionage thriller where time manipulation holds the key to preventing global catastrophe.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/k68nPLbIST6NP96JmTxmZijEvCA.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "In Bruges",
                    studio = "Focus Features",
                    rating = 8.0f,
                    description = "Two hitmen hide out in Bruges and discover unexpected beauty amid personal turmoil.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/o0TOcn35KQq0ft4WwcHC9nvdP8s.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "La La Land",
                    studio = "Summit Entertainment",
                    rating = 8.1f,
                    description = "A vibrant musical romance capturing the dreams and challenges of aspiring artists in Los Angeles.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/uDO1Q2yI0l3kivDJi2S9v7jS2.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "Whiplash",
                    studio = "Sony Pictures Classics",
                    rating = 8.5f,
                    description = "A determined drummer is pushed to his limits under the ruthless guidance of his instructor.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/o7ukJ0LHcXYV8V1z5Xh5RkoA3BU.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "The Grand Budapest Hotel",
                    studio = "Fox Searchlight Pictures",
                    rating = 8.1f,
                    description = "A quirky adventure following a legendary concierge and the theft of a priceless painting.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/3u6I96ZXR9JqSftK9Noqhzpg0mX.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "Mad Max: Fury Road",
                    studio = "Warner Bros.",
                    rating = 8.1f,
                    description = "A high-octane chase across a post-apocalyptic wasteland where survival is a daily battle.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/8tZYtuWezp8JbcsvHYO0O46tFbo.jpg",
                    isFavorite = false
                ),
                Movie(
                    name = "The Social Network",
                    studio = "Columbia Pictures",
                    rating = 7.7f,
                    description = "A captivating look at the rise of a tech visionary who reshaped online social connectivity.",
                    imageUrl = "https://image.tmdb.org/t/p/w500/2A02X7n38dYBKjeTr5BS9wEvWUr.jpg",
                    isFavorite = false
                )
            )

            sampleMovies.forEach { repo.addMovie(it) }
            repo.setSeededFlag()
        }
    }
}
