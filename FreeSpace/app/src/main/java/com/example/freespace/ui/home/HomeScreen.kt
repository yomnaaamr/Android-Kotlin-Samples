package com.example.freespace.ui.home

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.freespace.data.Note
import com.example.freespace.ui.login.isNetworkAvailable
import com.example.freespace.ui.navigation.NavigationDestination
import com.example.freespace.ui.note.DeleteConfirmationDialog
import com.example.freespace.ui.theme.FreeSpaceTheme
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


object HomeDestination : NavigationDestination {
    override val route = "home"
}

data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    navigateToNoteUpdate: (Int) -> Unit,
    navigateToNoteEntry: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
) {


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()

    val context = LocalContext.current
    var deleteAccountConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val items = listOf(
        NavigationItem(
            title = "تسجيل خروج",
            selectedIcon = Icons.Filled.Logout,
//            unselectedIcon = Icons.Outlined.Logout
            unselectedIcon = Icons.Default.Logout
        ),
        NavigationItem(
            title = "حذف الحساب",
            selectedIcon = Icons.Filled.DeleteForever,
            unselectedIcon = Icons.Default.DeleteForever
        )
    )


    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp
    // Calculate drawer width based on orientation
    val float = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        0.85f
    }
    else {
        0.50f
    }
    val drawerWidth = screenWidth * float


    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier
//            .widthIn(max = 300.dp)
//            .fillMaxSize(0.75f)
//            .width(drawerWidth)
        ,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier
                .widthIn(max= drawerWidth)
            )
            {
                Spacer(modifier = Modifier.height(16.dp))
                DrawerContent(user = Firebase.auth.currentUser)
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = item.title)
                        },
                        selected = true,
//                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            when (item) {
                                items.first() -> {
                                    if (isNetworkAvailable(context)) {
                                        viewModel.signOut(navController)
                                    } else {
                                        showToast(context, "لا يتوفر اتصال بالانترنت")
                                    }
                                }
                                else -> {
                                    deleteAccountConfirmationRequired = true
                                }
                            }
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        badge = {
                            item.badgeCount?.let {
                                Text(text = item.badgeCount.toString())
                            }
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .padding(bottom = 16.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            if (deleteAccountConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        deleteAccountConfirmationRequired = false
                        if (isNetworkAvailable(context)) {
                            viewModel.deleteAccount(navController)
                        } else {
                            showToast(context, "لا يتوفر اتصال بالانترنت")
                        }
                    },
                    onDeleteCancel = { deleteAccountConfirmationRequired = false },
                    modifier = Modifier.padding(5.dp)
                )
            }

        },
    ) {

        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "مساحتك الحرة",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            modifier = Modifier,
                            color = Color.White
                        )

                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceTint,
                    ),
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "menu",
                                tint = Color.White // Set the color to white
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = navigateToNoteEntry,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add note"
                    )
                }
            },
        ) { innerPadding ->

            LaunchedEffect(Unit) {
                if (!isNetworkAvailable(context)) {
                    showToast(context, "لا يتوفر اتصال بالانترنت")
                }
//                else{
//                    showToast(context, homeUiState.message)
//                }
            }


            HomeBody(
                notesList = homeUiState.itemList,
                onItemClick = navigateToNoteUpdate,
                modifier = Modifier
                    .padding(innerPadding)
//                .fillMaxSize()
            )


        }
    }


}


@Composable
fun DrawerContent(user: FirebaseUser?) {

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 18.dp)
            .padding(horizontal = 5.dp)
    ) {
        user?.photoUrl?.let {
            AsyncImage(model = it, contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } ?: Image(
            painter = rememberVectorPainter(image = Icons.Filled.Person),
            contentDescription = "User Photo",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = user?.displayName ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    Spacer(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceTint)
    )

    Spacer(modifier = Modifier.height(18.dp))

}


fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@Composable
fun HomeBody(
    notesList: List<Note>, onItemClick: (Int) -> Unit, modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        if (notesList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "",
//                    "اضغط + لإضافة ملاحظاتك.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                NotesList(
                    notesList = notesList, onItemClick = {
                        onItemClick(it.roomId)
                    },
                    modifier = Modifier
//                        .padding(horizontal = 3.dp, vertical = 3.dp)
                )
            }
        }
    }

}


@Composable
fun NotesList(
    notesList: List<Note>,
    onItemClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(4.dp)
    ) {

        items(items = notesList, key = { it.roomId }) { note ->
            NoteItem(note = note,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onItemClick(note) })
        }

    }

}


@Composable
fun NoteItem(note: Note, modifier: Modifier = Modifier) {

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    )

    {

        Column(
            modifier = Modifier
                .padding(12.dp)
        )
        {
            Text(
                text = note.title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 10,
                text = note.note,
//                textAlign = TextAlign.Justify
            )
        }

    }

}


@Preview
@Composable
private fun ItemPreview() {
    FreeSpaceTheme {
//        HomeScreen(navigateToNoteUpdate ={}, navigateToNoteEntry = { /*TODO*/ })
//        NoteItem(Note())
        DrawerContent(
            user = null
        )
    }
}