package com.example.booksapp.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booksapp.ui.auth.AuthScreen
import com.example.booksapp.ui.details.BookDetailsScreen
import com.example.booksapp.ui.favorites.FavoritesSharedViewModel
import com.example.booksapp.ui.feed.FeedScreen
import com.example.booksapp.ui.main.MainViewModel
import com.example.booksapp.ui.profile.ProfileScreen
import com.example.booksapp.ui.review.ReviewEditorScreen
import com.example.booksapp.ui.search.SearchScreen

private data class BottomItem(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
fun BooksNavGraph(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentUser = mainViewModel.currentUser.collectAsStateWithLifecycle().value
    val favoritesViewModel: FavoritesSharedViewModel = hiltViewModel()
    val favoriteIds = favoritesViewModel.favoriteIds.collectAsStateWithLifecycle().value

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()

    LaunchedEffect(currentUser?.uid) {
        if (currentUser == null) {
            navController.navigate(AppDestination.AUTH) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        } else if (currentRoute == AppDestination.AUTH || currentRoute.isBlank()) {
            navController.navigate(AppDestination.FEED) {
                popUpTo(AppDestination.AUTH) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val bottomItems = listOf(
        BottomItem(AppDestination.FEED, "Feed") { Icon(Icons.Default.Home, contentDescription = null) },
        BottomItem(AppDestination.SEARCH, "Search") { Icon(Icons.Default.Search, contentDescription = null) },
        BottomItem(AppDestination.PROFILE, "Profile") { Icon(Icons.Default.Person, contentDescription = null) },
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentUser != null && currentRoute in bottomItems.map { it.route }) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.AUTH,
            modifier = Modifier.padding(padding),
        ) {
            composable(AppDestination.AUTH) {
                AuthScreen()
            }
            composable(AppDestination.FEED) {
                FeedScreen(
                    snackbarHostState = snackbarHostState,
                    onBookClick = { book -> navController.navigate(AppDestination.details(book.id)) },
                )
            }
            composable(AppDestination.SEARCH) {
                SearchScreen(
                    snackbarHostState = snackbarHostState,
                    onBookClick = { book -> navController.navigate(AppDestination.details(book.id)) },
                    onToggleFavorite = { book -> favoritesViewModel.toggleFavorite(book) },
                    favoriteIds = favoriteIds,
                )
            }
            composable(AppDestination.PROFILE) {
                ProfileScreen(
                    onSignedOut = {
                        navController.navigate(AppDestination.AUTH) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                )
            }
            composable(
                route = AppDestination.DETAILS,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
            ) {
                val bookId = it.arguments?.getString("bookId").orEmpty()
                BookDetailsScreen(
                    currentUserId = currentUser?.uid,
                    snackbarHostState = snackbarHostState,
                    onOpenEditor = { comment ->
                        val route = if (comment == null) {
                            AppDestination.reviewEditor(bookId = bookId)
                        } else {
                            AppDestination.reviewEditor(
                                bookId = bookId,
                                commentId = comment.id,
                                initialRating = comment.rating,
                                initialText = Uri.encode(comment.text),
                            )
                        }
                        navController.navigate(route)
                    },
                )
            }
            composable(
                route = AppDestination.REVIEW_EDITOR,
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType },
                    navArgument("commentId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("initialRating") {
                        type = NavType.StringType
                        defaultValue = "5"
                    },
                    navArgument("initialText") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                ),
            ) {
                ReviewEditorScreen(
                    snackbarHostState = snackbarHostState,
                    onSaved = { navController.popBackStack() },
                )
            }
        }
    }
}
