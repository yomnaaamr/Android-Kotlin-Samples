package com.example.nyttoday.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nyttoday.ui.navigation.AccountInfo
import com.example.nyttoday.ui.navigation.NewsHomeScreen
import com.example.nyttoday.ui.navigation.SavedScreen
import com.example.nyttoday.ui.navigation.addHomeGraph
import com.example.nyttoday.ui.navigation.rememberNYTTodayNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainApp(mainNavHostController: NavHostController)
{

    val nestedNavController = rememberNYTTodayNavController()
    val isBottomBarVisible = rememberSaveable { mutableStateOf(true) }
    val onScrollToTop = remember {
        mutableStateOf({})
    }



    Scaffold(
        bottomBar = {
            BottomAppNavigationBar(
                nestedNavController.navController, isBottomBarVisible.value,
                navigateToRoute = {route->
                    nestedNavController.navigateToBottomBarRoute(route,onScrollToTop.value)
                },
            )
        }
    ) {

        NavHost(
            navController = nestedNavController.navController,
            startDestination = NewsHomeScreen
        ) {

            addHomeGraph(
                mainNavHostController = mainNavHostController,
                showNavigationBar = isBottomBarVisible,
                onScrollToTop = onScrollToTop
            )
        }
    }
}

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Composable
fun BottomAppNavigationBar(
    navController: NavController,
    isBottomBarVisible: Boolean,
    navigateToRoute: (String) -> Unit,
) {


    val imageUrl = Firebase.auth.currentUser?.photoUrl?.toString()
    val navigationBarItems = listOf(
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Main Page",
            screen = NewsHomeScreen
        ),
        BottomNavItem(
            icon = Icons.Default.Bookmarks,
            label = "Saved",
            screen = SavedScreen
        ),
        BottomNavItem(
            icon = Icons.Default.PersonPin,
            label = "Account Info",
            screen = AccountInfo,
            imgUrl = imageUrl
        )
    )
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination
////    var showBottomBar by remember { mutableStateOf(true) }


//    another easy way to check the current selected using items.forEachIndexed { index, item ->
    var selectedItemIndex by remember {
        mutableIntStateOf(0)
    }


    LaunchedEffect(currentDestination) {
//        showBottomBar = when (currentDestination?.route) {
//            LoginScreen.serializer().descriptor.serialName,
//            SignupScreen.serializer().descriptor.serialName -> false
//
//            else -> true
//        }
//
        // Update the selected item index based on the current screen class
        navigationBarItems.forEachIndexed { index, item ->
            if (currentDestination?.hierarchy?.any { it.route == item.screen::class.serializer().descriptor.serialName } == true) {
                selectedItemIndex = index
            }
        }
    }
//


//    if (showBottomBar) {
    AnimatedVisibility(
        visible = isBottomBarVisible,
        enter = fadeIn(),
        exit = fadeOut()
    )
    {
        NavigationBar(
            modifier = Modifier
                .height(56.dp),
        ) {
//
            navigationBarItems.forEachIndexed { index, item ->

                NavigationBarItem(
                    selected = selectedItemIndex == index
//                                currentDestination?.hierarchy?.any {
//                                    it.hasRoute(navItem.screen::class)
//                                } == true
                    ,
                    onClick = {
                        selectedItemIndex = index
                        navigateToRoute(item.screen::class.serializer().descriptor.serialName)

//                            navController.navigate(item.screen)
//                            {
////
//                                // Pop up to the start destination of the graph to avoid building up a large stack of destinations
//                                popUpTo(navController.graph.findStartDestination().id)
//                                {
////                                            inclusive = true
//                                    saveState = true
//
//                                }
//                                // Avoid multiple copies of the same destination when reselecting the same item
//                                launchSingleTop = true
//                                // Restore state when reselecting a previously selected item
//                                restoreState = true
//
//
//                            }
                    },
                    icon = {
//                                    Image(imageVector = navItem.icon, contentDescription =navItem.label)
                        item.imgUrl?.let {
                            AsyncImage(
                                model = ImageRequest.Builder(context = LocalContext.current)
                                    .data(it)
                                    .crossfade(true)
                                    .build(), contentDescription = item.label,
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp, if (selectedItemIndex == index)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            Color.Gray, CircleShape
                                    ),
                                contentScale = ContentScale.Crop,
                                placeholder = rememberVectorPainter(image = Icons.Default.PersonPin)
                            )
                        } ?: Image(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier
                                .background(Color.Transparent)
                                .size(30.dp),
                            contentScale = ContentScale.Crop,
                            colorFilter = if (selectedItemIndex == index)
                                ColorFilter.tint(MaterialTheme.colorScheme.primary) else ColorFilter.tint(
                                Color.Gray
                            )
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 10.sp
                            ),
                            fontWeight = if (selectedItemIndex == index) FontWeight.Black else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemColors(
                        selectedIconColor = Color.Red,
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Color.Gray,
                        selectedIndicatorColor = Color.Unspecified,
                        disabledIconColor = Color.Unspecified,
                        disabledTextColor = Color.Unspecified
                    )
//                                alwaysShowLabel = false,

                )
            }
        }
//        }
    }


}


data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val screen: Any,
    val imgUrl: String? = null
)
