package com.example.movieapp.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MovieRepository {
    private val db = FirebaseFirestore.getInstance()
    private val moviesCollection = db.collection("movies")

    fun getMovies() = callbackFlow {
        val listener = moviesCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val movies = snapshot?.documents?.mapNotNull { doc ->
                    val movie = doc.toObject(Movie::class.java)
                    movie?.copy(id = doc.id) // make sure ID is preserved
                } ?: emptyList()
                trySend(movies)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addMovie(movie: Movie) {
        // ✅ Ensure isFavorite is included in initial data
        moviesCollection.add(movie).await()
    }

    suspend fun updateMovie(movie: Movie) {
        // ✅ Update all fields INCLUDING isFavorite
        moviesCollection.document(movie.id).set(movie).await()
    }

    suspend fun updateMovieDescription(movieId: String, newDesc: String) {
        moviesCollection.document(movieId).update("description", newDesc).await()
    }

    suspend fun deleteMovie(movieId: String) {
        moviesCollection.document(movieId).delete().await()
    }

    suspend fun updateFavoriteStatus(movieId: String, isFavorite: Boolean) {
        // ✅ This enables toggling favorite
        moviesCollection.document(movieId).update("isFavorite", isFavorite).await()
    }

    suspend fun hasSeededMovies(): Boolean {
        val flagDoc = db.collection("app_config").document("seed_status").get().await()
        return flagDoc.getBoolean("seeded") == true
    }

    suspend fun migrateIsFavoriteFieldIfMissing() {
        val snapshot = moviesCollection.get().await()
        for (doc in snapshot.documents) {
            val data = doc.data ?: continue
            if (!data.containsKey("isFavorite")) {
                // Set default isFavorite to false if not present
                moviesCollection.document(doc.id).update("isFavorite", false).await()
            }
        }
    }



    suspend fun setSeededFlag() {
        db.collection("app_config").document("seed_status")
            .set(mapOf("seeded" to true)).await()
    }
}
