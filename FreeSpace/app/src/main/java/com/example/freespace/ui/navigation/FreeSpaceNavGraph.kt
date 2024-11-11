package com.example.freespace.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.freespace.ui.home.HomeDestination
import com.example.freespace.ui.home.HomeScreen
import com.example.freespace.ui.login.LoginDestination
import com.example.freespace.ui.login.LoginScreen
import com.example.freespace.ui.note.ItemEntryDestination
import com.example.freespace.ui.note.NoteDetailsDestination
import com.example.freespace.ui.note.NoteDetailsScreen
import com.example.freespace.ui.note.NoteEntryScreen
import com.example.freespace.ui.signup.SignUpDestination
import com.example.freespace.ui.signup.SignUpScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@SuppressLint("UnrememberedMutableState")
@Composable
fun FreeSpaceNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val startDestination = if(Firebase.auth.currentUser != null)
        HomeDestination.route else LoginDestination.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {


        composable(route = LoginDestination.route){
            LoginScreen(
                navigateToSignUp = {
                    navController.navigate(SignUpDestination.route)
                },
                navController = navController
            )
        }

        composable(route = SignUpDestination.route){
            SignUpScreen (
                alreadyHaveAccount = {
                    navController.navigate(LoginDestination.route)
                },
                navController = navController

            )
        }

        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToNoteEntry = { navController.navigate(ItemEntryDestination.route) },
                navigateToNoteUpdate = {
                    navController.navigate("${NoteDetailsDestination.route}/${it}")
                },
                navController = navController
            )
        }
        composable(route = ItemEntryDestination.route) {
            NoteEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = NoteDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(NoteDetailsDestination.noteIdArg) {
                type = NavType.IntType
            })
        ){
            NoteDetailsScreen(
                navigateBack = { navController.navigateUp()}
            )
        }
    }
}