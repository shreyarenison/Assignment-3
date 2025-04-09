package com.example.movieapp.ui

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movieapp.model.Movie
import com.example.movieapp.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMovieScreen(
    movieId: String?,
    viewModel: MovieViewModel = viewModel(),
    onDone: () -> Unit
) {
    val movies by viewModel.movies.collectAsState()
    val existingMovie = movies.find { it.id == movieId }
    val isEditing = existingMovie != null

    var name by remember { mutableStateOf(existingMovie?.name ?: "") }
    var studio by remember { mutableStateOf(existingMovie?.studio ?: "") }
    var rating by remember { mutableStateOf(existingMovie?.rating?.toString() ?: "") }
    var description by remember { mutableStateOf(existingMovie?.description ?: "") }
    var imageUrl by remember { mutableStateOf(existingMovie?.imageUrl ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Movie" else "Add Movie") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val updatedMovie = existingMovie?.copy(
                        name = name,
                        studio = studio,
                        rating = rating.toFloatOrNull() ?: 0f,
                        description = description,
                        imageUrl = imageUrl
                    ) ?: Movie(
                        name = name,
                        studio = studio,
                        rating = rating.toFloatOrNull() ?: 0f,
                        description = description,
                        imageUrl = imageUrl
                    )

                    if (isEditing) {
                        viewModel.updateMovie(updatedMovie)
                    } else {
                        viewModel.addMovie(updatedMovie)
                    }

                    onDone()
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            OutlinedTextField(value = studio, onValueChange = { studio = it }, label = { Text("Studio") })
            OutlinedTextField(value = rating, onValueChange = { rating = it }, label = { Text("Rating") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })

            if (isEditing) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        }
    }

    if (showDeleteDialog && isEditing) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Movie") },
            text = { Text("Are you sure you want to delete this movie?") },
            confirmButton = {
                TextButton(onClick = {
                    existingMovie?.id?.let { viewModel.deleteMovie(it) }
                    showDeleteDialog = false
                    onDone()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}
