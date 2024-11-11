package com.example.freespace.ui.note

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.freespace.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


object ItemEntryDestination : NavigationDestination {
    override val route = "item_entry"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEntryScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()
    val viewModel: NoteEntryViewModel = hiltViewModel<NoteEntryViewModel>()


    Scaffold(
        topBar = {
            NoteTopBar(
                navigateUp = onNavigateUp,
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.saveNote()
                        navigateBack()
                    }
                },
                hasNote = viewModel.validateInput(),
            )
        }
    ) {innerPadding ->

        NoteItem(modifier = modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            ,
            noteUiState = viewModel.noteUiState,
            onValueChange = viewModel::updateUiState,
            gotClicked = {}

        )

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTopBar(
    modifier: Modifier = Modifier,
//    canNavigateBack: Boolean,
    hasNote: Boolean = false,
    onSaveClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}) {

    TopAppBar(
        title = {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                if(hasNote){

                    OutlinedButton(
                        onClick = onSaveClick,
                        shape = MaterialTheme.shapes.extraLarge,
                        border = BorderStroke(0.dp, Color.Transparent)
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        containerColor = Color.Black,
//                        contentColor = Color.White)
                    )
                    {
//                        Text(
//                            text = "حفظ",
//                            modifier = Modifier.padding(end = 5.dp),
//                            fontSize = 15.sp
//                        )
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Done Add",
                            modifier = Modifier.size(25.dp)
                        )

                    }
                }
            }
                },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )

}


@Composable
fun NoteItem(modifier: Modifier = Modifier,
             noteUiState: NoteUiState,
             onValueChange: (NoteDetails) -> Unit,
             gotClicked: () -> Unit,
) {


    Column(modifier = modifier
        .imePadding()
        .fillMaxSize()
//        .padding(vertical = 4.dp)
    ){


        TextField(
            value =noteUiState.noteDetails.title,
            onValueChange = {
                            onValueChange(noteUiState.noteDetails.copy(title = it))
            } ,
            placeholder = { Text(text = "العنوان") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        gotClicked()
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
//            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,

        )

//        Spacer(modifier = Modifier.height(5.dp))

        TextField(value = noteUiState.noteDetails.note,
            onValueChange = {
                            onValueChange(noteUiState.noteDetails.copy(note = it))
            },
            placeholder = { Text(text = "مساحة حرة")},
            modifier = Modifier
//                .fillMaxSize()
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        gotClicked()
                    }
                },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            textStyle = MaterialTheme.typography.bodySmall,
//            maxLines = (1)


        )
    }

}
