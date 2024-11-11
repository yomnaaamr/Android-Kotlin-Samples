package com.example.freespace

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.freespace.ui.navigation.FreeSpaceNavHost


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FreeSpaceApp(
    navController: NavHostController = rememberNavController()
) {
    FreeSpaceNavHost(navController = navController)
}





