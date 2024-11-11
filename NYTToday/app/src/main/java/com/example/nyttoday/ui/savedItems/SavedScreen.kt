package com.example.nyttoday.ui.savedItems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nyttoday.R
import com.example.nyttoday.data.Article
import com.example.nyttoday.ui.home.HomeScreenTopBar
import com.example.nyttoday.ui.theme.Georgia
import com.example.nyttoday.ui.theme.NYTTodayTheme
import com.example.nyttoday.util.Resource


@Composable
fun SavedScreen(
    viewModel: SavedScreenViewModel = hiltViewModel(),
){

    val savedUiState by viewModel.savedUiState.collectAsStateWithLifecycle()

    SavedScreenContent( savedUiState = savedUiState ) {
        
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreenContent(
    modifier: Modifier = Modifier,
    savedUiState: Resource<List<Article>>,
    clickedItemUrl: (String) -> Unit,

    ) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeScreenTopBar(
                scrollBehavior = scrollBehavior,
                painter = painterResource(id = R.drawable.fontboltsaved),
            )
        }
    ) { innerPadding ->


        when (savedUiState) {

            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize()){
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            is Resource.Success ->
                savedUiState.data?.let {
                    SavedList(savedList = it,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                    }
                }

            is Resource.Error -> TODO()
        }




    }

}

@Composable
fun SavedList(
    modifier: Modifier = Modifier,
    savedList: List<Article>,
    clickedItemUrl: (String) -> Unit,
) {

    LazyColumn(
        modifier.fillMaxSize(),
    ) {
        items(savedList) { savedItem ->
            SavedItem(
                savedItem, modifier = Modifier.fillMaxSize(),
                clickedItemUrl = clickedItemUrl
            )
        }
    }
}

@Composable
fun SavedItem(
    savedItem: Article,
    modifier: Modifier = Modifier,
    clickedItemUrl: (String) -> Unit,
) {

    Box(modifier = modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 1.dp),
            shape = MaterialTheme.shapes.extraSmall.copy(
                bottomEnd = CornerSize(0.dp),
                bottomStart = CornerSize(0.dp)
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            onClick = {
                clickedItemUrl(savedItem.url!!)
            },
            border = BorderStroke(width = 1.dp, color = Color.Transparent)
        ) {

           Row(
               modifier = Modifier.padding(10.dp)
           ) {


               Column(
                   modifier = Modifier.weight(2f)
               ) {

                   Text(
                       text = savedItem.title!!,
                       style = MaterialTheme.typography.bodyLarge.copy(
                           fontFamily = Georgia,
                           fontWeight = FontWeight.ExtraBold,
                           color = Color.Black,
                       ),
//                       lineHeight = 30.sp,
//                textAlign = TextAlign.Justify
                   )

                   Spacer(modifier = Modifier.height(8.dp))

                   Text(
                       text = savedItem.abstract!!,
                       style = MaterialTheme.typography.bodyLarge.copy(
                           fontSize = 12.sp
                       ),
//                       lineHeight = 24.sp,
                   )

                   Spacer(modifier = Modifier.height(8.dp))

                   Text(
                       text = savedItem.byline!!,
                       style = MaterialTheme.typography.bodyLarge.copy(
                           fontFamily = Georgia,
                           fontWeight = FontWeight.Light,
                           fontSize = 9.sp,
                       )
                   )

                   Spacer(modifier = Modifier.height(16.dp))



                   Spacer(modifier = Modifier.height(8.dp))

//                   Text(
//                       text = newItem.multimedia?.firstOrNull()?.copyright
//                           ?: "No copyright information available",
//                       style = MaterialTheme.typography.bodyLarge.copy(
//                           fontFamily = Georgia,
//                           fontWeight = FontWeight.Normal,
//                           fontSize = 9.sp
//                       ),
//                       modifier = Modifier.align(Alignment.End)
//                   )

               }

               Spacer(modifier = Modifier.width(8.dp))

               val imageUrl = savedItem.imageUrl
               AsyncImage(
                   model = ImageRequest.Builder(context = LocalContext.current)
                       .data(imageUrl)
                       .crossfade(true)
                       .build(),
                   contentDescription = null,
                   modifier = Modifier
                       .size(150.dp)
                       .weight(1f)
                       .fillMaxHeight(),
                   contentScale = ContentScale.Crop,
                   error = painterResource(R.drawable.ic_broken_image),
                   placeholder = painterResource(R.drawable.loading_img),
               )

           }

            HorizontalDivider(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = Color.Gray
            )

        }



    }

}


@Preview
@Composable
private fun SavedScreenPreview() {
    NYTTodayTheme {





        SavedScreenContent (savedUiState = Resource.Loading()){

        }

//        SavedList(savedList = sampleResults) {
//
//        }

//        SavedItem(savedItem = ResultsItem(
//            title = "Sample Title 1",
//            abstract = "This is a sample abstract for the first item.",
//            byline = "Author 1",
//            url = "https://example.com/1",
//            multimedia = listOf(
//                MultimediaItem(
//                    url = "https://example.com/image1.jpg",
//                    format = "Standard Thumbnail",
//                    copyright = "Copyright 1"
//                )
//            )
//        )) {
//
//        }
    }
}