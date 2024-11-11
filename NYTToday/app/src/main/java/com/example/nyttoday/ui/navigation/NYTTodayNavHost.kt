package com.example.nyttoday.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.nyttoday.ui.MainApp
import com.example.nyttoday.ui.account.AccountScreen
import com.example.nyttoday.ui.home.HomeScreen
import com.example.nyttoday.ui.login.LoginScreen
import com.example.nyttoday.ui.savedItems.SavedScreen
import com.example.nyttoday.ui.signup.SignUpScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.Serializable

@SuppressLint("RestrictedApi")
@Composable
fun AppNavHost(navController: NavHostController)
{


    val startDestination = if (Firebase.auth.currentUser != null)
        Home::class else Auth::class


    NavHost(navController = navController, startDestination = startDestination) {


        composableWithCompositionLocal {
//            val viewModel = it.sharedViewModel<HomeViewModel>()
//            val showNavigationBarState by viewModel.showNavigationBar.collectAsStateWithLifecycle()
            MainApp(mainNavHostController = navController)
        }


        authGraph(navController)
    }

}

fun NavGraphBuilder.addHomeGraph(
    mainNavHostController: NavHostController,
    showNavigationBar: MutableState<Boolean>,
    onScrollToTop: MutableState<() -> Unit>,

) {

    composable<NewsHomeScreen> {
        HomeScreen(
            showNavigationBar =showNavigationBar,
            onScrollToTop = onScrollToTop,
        )

    }



    composable<AccountInfo> {
        AccountScreen(
            logoutNavigate = {
                mainNavHostController.navigate(Auth) {
                    popUpTo(Home) { inclusive = true }
                }
            },
        )
    }

    composable<SavedScreen> {

        SavedScreen()
    }


}


private fun NavGraphBuilder.authGraph(navController: NavHostController)
{
    navigation<Auth>(startDestination = LoginScreen)
    {


        composable<LoginScreen> {
            LoginScreen(
                navigateToMainScreen = {
                    navController.navigate(Home) {
                        popUpTo(Auth) {
                            inclusive = true
                        }
                    }
                },
                navigateToSignUp = {
                    navController.navigate(SignupScreen)
                })
        }

        composable<SignupScreen> {
            SignUpScreen(
                navigateToMainScreen = {
                    navController.navigate(Home) {
                        popUpTo(Auth) {
                            inclusive = true
                        }
                    }
                }, alreadyHaveAccount = {
                    navController.navigate(LoginScreen)

                })
        }

    }
}


fun NavGraphBuilder.composableWithCompositionLocal(
//    route: KClass<*>,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? = {
        fadeIn(nonSpatialExpressiveSpring())
    },
    exitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? = {
        fadeOut(nonSpatialExpressiveSpring())
    },
    popEnterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        enterTransition,
    popExitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {


    composable<Home>(
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        deepLinks = deepLinks
    ) {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }

//    composable(
//        Json.decodeFromString(route),
//        arguments,
//        deepLinks,
//        enterTransition,
//        exitTransition,
//        popEnterTransition,
//        popExitTransition
//    ) {
//        CompositionLocalProvider(
//            LocalNavAnimatedVisibilityScope provides this@composable
//        ) {
//            content(it)
//        }
//    }

}


val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

fun <T> nonSpatialExpressiveSpring() = spring<T>(
    dampingRatio = 1f,
    stiffness = 1600f
)


//to share the same viewmodel instance between screens
//@Composable
//inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
//    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
//    val parentEntry = remember(this) {
//        navController.getBackStackEntry(navGraphRoute)
//    }
//    return hiltViewModel(parentEntry)
//}

//@Composable
//inline fun <reified VM : ViewModel> NavBackStackEntry.sharedViewModel(): VM {
//    return hiltViewModel(this)
//}


//@Composable
//inline fun <reified VM : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): VM {
//    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
//    val parentEntry = remember(this) {
//        navController.getBackStackEntry(navGraphRoute)
//    }
//    return hiltViewModel(parentEntry)
//}


@Serializable
object LoginScreen

@Serializable
object SignupScreen

@Serializable
object NewsHomeScreen

@Serializable
object AccountInfo

@Serializable
object SavedScreen

@Serializable
object Auth

@Serializable
object Home

//@Serializable
//data class ScreenB(
//    val name: String?,
//    val age: Int
//)