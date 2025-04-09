package com.example.movieapp.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movieapp.viewmodel.MovieViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val movieViewModel: MovieViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("movieList") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("movieList") {
            MovieListScreen(
                navController = navController,
                viewModel = movieViewModel
            )
        }

        composable("favorites") {
            FavoritesOnlyScreen(
                navController = navController,
                viewModel = movieViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("favoriteDetails/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")
            movieId?.let {
                FavoritesDetailsScreen(
                    movieId = it,
                    viewModel = movieViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable("addEdit/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")
            AddEditMovieScreen(
                movieId = movieId,
                onDone = { navController.popBackStack() },
                viewModel = movieViewModel
            )
        }
    }
}
