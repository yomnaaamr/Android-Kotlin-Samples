package com.example.nyttoday

import androidx.compose.runtime.Composable
import com.example.nyttoday.ui.navigation.AppNavHost
import com.example.nyttoday.ui.navigation.rememberNYTTodayNavController


@Composable
fun NYTTodayApp() {

    val navController = rememberNYTTodayNavController()

    AppNavHost(navController = navController.navController)


    /**
     * Understanding the Issue
     * Before we dive into solutions, let's recap the problem:
     *
     * You have a StateFlow in your Home Screen ViewModel that determines whether to show the bottom bar.
     * This StateFlow is updated based on the scroll state of a LazyColumn in the Home Screen.
     * You're trying to collect this StateFlow in the NYTTodayApp composable (which contains the navigation host) to control the bottom bar visibility.
     * However, the StateFlow value isn't updating as expected in NYTTodayApp, and work very fine in home screen composable.
     *
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
     *
     * The Problem: Multiple ViewModel Instances
     * By injecting the HomeViewModel using hiltViewModel in both the HomeScreen and NYTTodayApp composables,
     * you were inadvertently creating two separate instances of the ViewModel.
     * This led to inconsistent state management and prevented the StateFlow from being updated correctly in the NYTTodayApp.
     *
     * The Solution: Single ViewModel Instance
     * By injecting the HomeViewModel at a higher level and passing it to both composables,
     * you ensured that both components were sharing the same ViewModel instance.
     * This allowed the StateFlow updates to be propagated correctly to the NYTTodayApp.
     */


}

