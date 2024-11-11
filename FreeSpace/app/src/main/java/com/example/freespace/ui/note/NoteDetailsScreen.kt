package com.example.freespace.ui.note

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.freespace.R
import com.example.freespace.ui.navigation.NavigationDestination
import com.example.freespace.ui.theme.FreeSpaceTheme
import kotlinx.coroutines.launch

object NoteDetailsDestination : NavigationDestination {
    override val route = "note_details"
    const val noteIdArg = "itemId"
    val routeWithArgs = "$route/{$noteIdArg}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    viewModel: NoteDetailsViewModel = hiltViewModel<NoteDetailsViewModel>()
) {
    
    
//    val uiState = viewModel.uiState.collectAsState()
    val noteUpdateUiState = viewModel.itemUiState
    val coroutineScope = rememberCoroutineScope()
    var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
    val gotClicked = viewModel.isNoteItemClicked.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()



    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {

                        Text(text = stringResource(id = R.string.note_date,
                            noteUpdateUiState.noteDetails.data,
                            noteUpdateUiState.noteDetails.time),
                            fontSize = 11.sp,
//                          maxLines = 2,
                            lineHeight = 14.sp,
                            style = MaterialTheme.typography.bodySmall,
//                            modifier = Modifier.width(110.dp),
                            textAlign = TextAlign.Center
                        )

                        OutlinedButton(
                            onClick = { deleteConfirmationRequired = true },
                            shape = MaterialTheme.shapes.extraLarge,
                            border = BorderStroke(0.dp, Color.Transparent),
//                            modifier = Modifier.padding(start = 10.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = " delete",
                                modifier = Modifier.size(25.dp)
                            )

                        }

                        if(gotClicked.value && viewModel.validateInput()){
                            OutlinedButton(
                                onClick = {
                                          coroutineScope.launch {
                                              viewModel.updateNote()
                                              navigateBack()
                                          }
                                } ,
                                shape = MaterialTheme.shapes.extraLarge,
                                border = BorderStroke(0.dp, Color.Transparent)
                            )
                            {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "update",
                                    modifier = Modifier.size(25.dp)
                                )

                            }
                        }

                        if (deleteConfirmationRequired) {

                            DeleteConfirmationDialog(
                                onDeleteConfirm = {
                                    deleteConfirmationRequired = false
                                    coroutineScope.launch {
                                        viewModel.deleteNote()
                                        navigateBack()
                                    }
                                },
                                onDeleteCancel = { deleteConfirmationRequired = false },
                                modifier = Modifier.padding(5.dp)
                            )
                        }

                    }
                }, navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "back button"
                        )
                    }
                })
        },
    ) {innerPadding ->
        
        NoteItem(
            noteUiState = noteUpdateUiState.noteDetails.toNote().toNoteUiState(),
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
            ,
            onValueChange = viewModel::updateUiState,
            gotClicked = { viewModel.onTextFieldClicked() }
        )
        
    }


}

@Composable
fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier
) {


    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        }
    )

}


@Preview
@Composable
private fun NoteDetailPreview() {
    FreeSpaceTheme {
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 5.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        )
        {

            Text(text = stringResource(id = R.string.note_date,
                " 24-6-2024 ",
                 "5:40:00"),
                fontSize = 11.sp,
//                maxLines = 2,
                lineHeight = 14.sp,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
//                    .height(50.dp)
//                    .width(110.dp)
                ,
                textAlign = TextAlign.Center
            )

            OutlinedButton(
                onClick = {  },
                shape = MaterialTheme.shapes.extraLarge,
                border = BorderStroke(0.dp, Color.Transparent),
//                            modifier = Modifier.padding(start = 10.dp)

            ) {

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = " delete",
                    modifier = Modifier.size(25.dp)
                )

            }
        }
    }
}