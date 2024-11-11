package com.example.nyttoday.ui.account

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nyttoday.NYTTodayApplication
import com.example.nyttoday.R
import com.example.nyttoday.ui.login.UiState
import com.example.nyttoday.ui.login.isNetworkAvailable
import com.example.nyttoday.ui.theme.NYTTodayTheme
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    logoutNavigate: ()-> Unit,
    viewModel: AccountViewModel = hiltViewModel<AccountViewModel>()
) {


    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext  // Use application context to avoid memory leaks
    val application = context as NYTTodayApplication
    val successState by viewModel.uiStateAccount.collectAsStateWithLifecycle()


    LaunchedEffect(successState) {
        if(successState is UiState.Success){
            logoutNavigate()
            application.cancelNewsRefresh()
        }
    }


    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "Account Info",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp
                    )
                )
            },
                navigationIcon = {
//                    IconButton(onClick = navigateBack) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "back button"
//                        )
//                    }
                })
        },

    ) {



        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp)
        ) {

            AccountInfo(user = Firebase.auth.currentUser)

            ElevatedButton(
                onClick = {
                    if (isNetworkAvailable(context)) {
                        viewModel.signOut()
                    } else {
                        showToast(context, context.getString(R.string.no_internet))
                    }
                },
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = modifier.padding(end = 10.dp)
                    )
                    Text(text = stringResource(id = R.string.logout),
                        style = MaterialTheme.typography.bodyLarge)
                }


            }


            Spacer(modifier = Modifier.height(18.dp))

            ElevatedButton(
                onClick = {
                    if(isNetworkAvailable(context)) deleteConfirmationRequired = true
                    else showToast(context, context.getString(R.string.no_internet))
                },
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        modifier = modifier.padding(end = 10.dp)
                    )
                    Text(text = stringResource(id = R.string.delete_account),
                        style = MaterialTheme.typography.bodyLarge)
                }

            }

        }



        if (deleteConfirmationRequired) {

            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    coroutineScope.launch {
                        viewModel.deleteAccount()
                    }
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(5.dp)
            )
        }
    }

}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier
) {


    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention),
            style = MaterialTheme.typography.bodyLarge) },
        text = { Text(stringResource(R.string.delete_question),
            style = MaterialTheme.typography.bodyLarge) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no),
                    style = MaterialTheme.typography.bodyLarge)
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes),
                    style = MaterialTheme.typography.bodyLarge)
            }
        }
    )

}



@Composable
fun AccountInfo(user: FirebaseUser?) {

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 18.dp)
            .padding(horizontal = 5.dp)
    ) {
        user?.photoUrl?.let {
            AsyncImage(model = ImageRequest.Builder(context = LocalContext.current)
                .data(it)
                .crossfade(true)
                .build()
                , contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } ?: Image(
            painter = rememberVectorPainter(image = Icons.Default.PersonPin),
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
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp
                )
            )
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp
                )
            )
        }
    }

    Spacer(
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceTint)
//            .absoluteOffset(x = (-28).dp, y = (-28).dp) // Adjust the offset to negate the parent's padding
////            .padding(0.dp)
    )

    Spacer(modifier = Modifier.height(18.dp))

}

@Preview
@Composable
private fun AccountScreenPreview() {
    NYTTodayTheme {

    }
}