package com.felippeneves.readerapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.felippeneves.readerapp.screens.ReaderSplashScreen
import com.felippeneves.readerapp.screens.details.ReaderBookDetailsScreen
import com.felippeneves.readerapp.screens.home.ReaderHomeScreen
import com.felippeneves.readerapp.screens.home.ReaderHomeViewModel
import com.felippeneves.readerapp.screens.login.ReaderLoginScreen
import com.felippeneves.readerapp.screens.search.ReaderBookSearchScreen
import com.felippeneves.readerapp.screens.search.ReaderBookSearchViewModel
import com.felippeneves.readerapp.screens.stats.ReaderStatsScreen
import com.felippeneves.readerapp.screens.update.ReaderBookUpdateScreen
import com.felippeneves.readerapp.utils.Constants

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name) {
        composable(ReaderScreens.SplashScreen.name) {
            ReaderSplashScreen(navController = navController)
        }

        composable(ReaderScreens.LoginScreen.name) {
            ReaderLoginScreen(navController = navController)
        }

        composable(ReaderScreens.ReaderHomeScreen.name) {
            val homeViewModel = hiltViewModel<ReaderHomeViewModel>()
            ReaderHomeScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }

        composable(ReaderScreens.SearchScreen.name) {
            val searchViewModel = hiltViewModel<ReaderBookSearchViewModel>()
            ReaderBookSearchScreen(
                navController = navController,
                viewModel = searchViewModel
            )
        }


        val detailName = ReaderScreens.DetailScreen.name
        composable(
            route = "$detailName/{${Constants.BOOK_ID_ARGUMENT}}",
            arguments = listOf(navArgument(Constants.BOOK_ID_ARGUMENT) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString(Constants.BOOK_ID_ARGUMENT).let { bookId ->
                ReaderBookDetailsScreen(
                    navController = navController,
                    bookId = bookId.toString()
                )
            }
        }

        val updateName = ReaderScreens.UpdateScreen.name
        composable(
            route = "$updateName/{${Constants.BOOK_ITEM_ID_ARGUMENT}}",
            arguments = listOf(navArgument(Constants.BOOK_ITEM_ID_ARGUMENT) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString(Constants.BOOK_ITEM_ID_ARGUMENT).let { bookItemId ->
                ReaderBookUpdateScreen(
                    navController = navController,
                    bookItemId = bookItemId.toString()
                )
            }
        }

        composable(ReaderScreens.ReaderStatsScreen.name) {
            val homeViewModel = hiltViewModel<ReaderHomeViewModel>()
            ReaderStatsScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }
    }
}