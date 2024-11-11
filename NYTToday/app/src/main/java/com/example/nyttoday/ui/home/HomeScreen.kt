package com.example.nyttoday.ui.home

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nyttoday.R
import com.example.nyttoday.data.Article
import com.example.nyttoday.data.MultimediaItem
import com.example.nyttoday.data.ResultsItem
import com.example.nyttoday.ui.theme.Georgia
import com.example.nyttoday.ui.theme.NYTTodayTheme
import com.example.nyttoday.util.Resource
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    showNavigationBar: MutableState<Boolean>,
    onScrollToTop: MutableState<() -> Unit>,
) {

    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val clickedItem by viewModel.clickedItemUrl.collectAsStateWithLifecycle()
    val isShownBottomSheet by viewModel.isShownBottomSheet.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
//    val showNavigationBarState by viewModel.showNavigationBar.collectAsStateWithLifecycle()

//    val news = viewModel.newsPagingFlow.collectAsLazyPagingItems()


    HomeContent(
        homeUiState = homeUiState,
        isShownBottomSheet = isShownBottomSheet,
        clickedItem = clickedItem,
        showNavigationBar = showNavigationBar,
//        { isVisible ->
////            viewModel.updateNavigationBarState(isVisible)
////            showNavigationBar(isVisible)
//            showNavigationBar.value = isVisible
//        },

        clickedItemUrl = {
            viewModel.updateClickedItemUrlState(it)
            viewModel.updateIsShownBottomSheetState(true)
        },
        updateIsShownBottomSheetState = {
            viewModel.updateIsShownBottomSheetState(false)
        },
        fetchNewData = {
            coroutineScope.launch {
                viewModel.refreshNews()
            }
        },
        isRefreshing = isRefreshing,
        modifier = modifier,
        saveArticle = {
            coroutineScope.launch {
                viewModel.saveArticle(clickedItem)
            }
        },
        onScrollToTop = onScrollToTop,
//        news = news
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    homeUiState: Resource<List<Article>>,
    isShownBottomSheet: Boolean,
    clickedItem: String,
//    showNavigationBar: (Boolean) -> Unit,
    showNavigationBar: MutableState<Boolean>,
    clickedItemUrl: (String) -> Unit,
    updateIsShownBottomSheetState: () -> Unit,
    fetchNewData: () -> Unit,
    isRefreshing: Boolean,
    saveArticle: () -> Unit,
//    onScrollToTop: ( () -> Unit) -> Unit,
    onScrollToTop: MutableState<() -> Unit>,
//    isFabVisible: (Boolean) -> Unit
//    news: LazyPagingItems<Article>

) {

    val sheetState = rememberModalBottomSheetState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    val isFabVisible = rememberSaveable { mutableStateOf(false) }


    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeScreenTopBar(
                scrollBehavior,
                painterResource(id = R.drawable.fontbolt)
            )
        },
        floatingActionButton = {

                AnimatedVisibility (isFabVisible.value) {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(bottom = if (showNavigationBar.value) 56.dp else 0.dp),
                        onClick = {
                            onScrollToTop.value.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Scroll to top"
                        )
                    }
                }

        }
    ) { innerPadding ->

//
//        if (news.loadState.refresh is LoadState.Loading){
//            LoadingSkeleton(modifier = Modifier.padding(innerPadding))
//        }else{
//            NewsList(
//                newsList = news,
////                    newsList = homeUiState.newsList,
//                modifier = Modifier
//                    .padding(innerPadding)
//                    .fillMaxSize(),
//                showNavigationBar = showNavigationBar,
//                clickedItemUrl = clickedItemUrl,
//                isRefreshing = isRefreshing,
//                onRefresh = fetchNewData
//            )
//        }


        when (homeUiState) {
            is Resource.Loading -> {

                LoadingSkeleton(modifier = Modifier.padding(innerPadding))
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(
//                        progress = {
//                            0.8f
//                        },
//                                modifier = Modifier.padding(innerPadding),
//                        strokeWidth = 3.dp,
//                    )
//                }
            }

            is Resource.Success -> {
                homeUiState.data?.let {
                    NewsList(
            //                    newsList = news,
                        newsList = it,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        showNavigationBar = { visible->
                            showNavigationBar.value = visible
//                            showNavigationBar(visible)
//                            isBottomBarVisible = visible
                        },
                        clickedItemUrl = clickedItemUrl,
                        isRefreshing = isRefreshing,
                        onRefresh = fetchNewData,
                        onScrollToTop = {scroll->
                            onScrollToTop.value = scroll
//                            onScrollToTop(it)
                        },
                        isFabVisible = {fab->
                            isFabVisible.value = fab
                        }
                    )
                }

            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    homeUiState.message?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier
                                .padding(innerPadding)
                        )
                    }
                }
            }

        }



        if (isShownBottomSheet) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
//                    webViewState.value?.apply {
//                        stopLoading()
//                        clearHistory()
//                        removeAllViews()
//                        post {
//                            destroy() // Destroy the WebView after it's detached from the window
//                        }
////                        destroy()
////                        loadUrl("about:blank")
//                    }
//                    webViewState.value = null
                    updateIsShownBottomSheetState()
                },
                dragHandle = {
                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                saveArticle()
                            }
                    ) {

                        Icon(
                            imageVector = Icons.Default.Bookmarks,
                            contentDescription = null,
                            modifier = Modifier
                        )
                        Text(
                            text = "Click To Save",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
            ) {
//            selectedNewsUrl?.let { url ->
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                context.startActivity(intent)


//                NewsDetailWebView(
//                    url = clickedItem,
//                    modifier = Modifier.fillMaxSize(),
//                    webViewState = webViewState
//                )




                LazyColumn {
                    item {
                        WebViewScreen(
                            url = clickedItem
                        )
                    }

                }



//                LazyColumn {
//                    item {
//                        NewsDetailWebView(
//                            url = clickedItem,
//                            modifier = Modifier.fillMaxSize(),
//                            webViewState = webViewState
//                        )
//                    }
//                }

            }
        }


//        NewsList(
//            newsList = homeUiState.newsList,
//            modifier = Modifier.padding(innerPadding)
//                .fillMaxSize()
//        )


    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    painter: Painter
) {

    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Image(
                painter = painter,
//                painterResource(id = R.drawable.fontbolt),
                contentDescription = null,
                modifier = Modifier
                    .width(220.dp),
            )

        },
        navigationIcon = {
//            IconButton(onClick = {
//                Firebase.auth.signOut()
//            }) {
//                Image(painter = painterResource(id = R.drawable.nyt)
//                    , contentDescription = null,
//                    modifier = Modifier
//                        .size(34.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop,
//                )
//            }
        })


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsList(
    newsList: List<Article>,
//    newsList: LazyPagingItems<Article>,
    modifier: Modifier = Modifier,
    showNavigationBar: (Boolean) -> Unit,
    clickedItemUrl: (String) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onScrollToTop: ( () -> Unit) -> Unit,
    isFabVisible: (Boolean) -> Unit
) {

//    val context = LocalContext.current


//    LaunchedEffect(key1 = newsList.loadState) {
//        if(newsList.loadState.refresh is LoadState.Error) {
//            Toast.makeText(
//                context,
//                "Error: " + (newsList.loadState.refresh as LoadState.Error).error.message,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

    val isScrollingUp = remember { mutableStateOf(true) }

    val previousIndex = remember { mutableIntStateOf(0) }
    val previousOffset = remember { mutableIntStateOf(0) }
    val isFirstScrollEvent = remember { mutableStateOf(true) }


//    // Update FAB visibility based on scroll position
//    LaunchedEffect(lazyListState) {
//        snapshotFlow { lazyListState.firstVisibleItemIndex > 0 }
//            .collect { isNotAtTop ->
//                isFabVisible(isNotAtTop)
//            }
//    }

    val showScrollToTopButton by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
    }

    LaunchedEffect(showScrollToTopButton) {
        isFabVisible(showScrollToTopButton)
    }


    // Trigger scroll to top when requested
    LaunchedEffect(Unit) {
        onScrollToTop {
            scope.launch {
                lazyListState.animateScrollToItem(0)
            }
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
        }
//            .debounce(50) // Add debounce to limit the recomposition frequency
            .collect { (firstVisibleItemIndex, firstVisibleItemScrollOffset) ->
                if (isFirstScrollEvent.value) {
                    // Skip the first event to avoid unwanted hiding/showing
                    isFirstScrollEvent.value = false
                } else {
                    val isCurrentlyScrollingUp =
                        if (firstVisibleItemIndex != previousIndex.intValue) {
                            firstVisibleItemIndex < previousIndex.intValue
                        } else {
                            firstVisibleItemScrollOffset < previousOffset.intValue
                        }

                    if (isScrollingUp.value != isCurrentlyScrollingUp) {
                        isScrollingUp.value = isCurrentlyScrollingUp
                        showNavigationBar(isCurrentlyScrollingUp)
                    }
                }

                previousIndex.intValue = firstVisibleItemIndex
                previousOffset.intValue = firstVisibleItemScrollOffset
            }
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {

        LazyColumn(
            Modifier.fillMaxSize(),
            state = lazyListState
        ) {


            items(newsList){item->
                NewItem(
                    newItem = item,
                    modifier = Modifier.fillMaxSize(),
                    clickedItemUrl = clickedItemUrl
                )

            }



//
//            item{
//                if(newsList.loadState.append is LoadState.Loading){
//                    CircularProgressIndicator()
//                }
//            }

//            items(newsList) { newsItem ->
//                NewItem(
//                    newsItem, modifier = Modifier.fillMaxSize(),
//                    clickedItemUrl = clickedItemUrl
//                )
//            }
        }


        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onRefresh()
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter),
        )

    }


}


@Composable
fun NewItem(
    newItem: Article,
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
                clickedItemUrl(newItem.url!!)
            },
            border = BorderStroke(width = 1.dp, color = Color.Transparent)
        ) {

            Column(
                modifier = Modifier.padding(15.dp)
            ) {

                Text(
                    text = newItem.title!!,
                    style = MaterialTheme.typography.headlineLarge,
                    lineHeight = 30.sp,
//                textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = newItem.abstract!!,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = newItem.byline!!,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = Georgia,
                        fontWeight = FontWeight.Light,
                        fontSize = 9.sp,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))


                val imageUrl = newItem.imageUrl
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2048f / 1366f),
//                        .height(400.dp),
//                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_broken_image),
                    placeholder = painterResource(R.drawable.loading_img),
                )


                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = newItem.copyright
                        ?: "No copyright information available",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = Georgia,
                        fontWeight = FontWeight.Normal,
                        fontSize = 9.sp
                    ),
                    modifier = Modifier.align(Alignment.End)
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


@Composable
fun LoadingSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(5) {
            SkeletonItem()
        }
    }
}


fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
//                Color(0xFFB8B5B5),
//                Color(0xFF8F8B8B),
//                Color(0xFFB8B5B5),
                Color.LightGray.copy(alpha = 0.6f),
                Color.LightGray.copy(alpha = 0.2f),
                Color.LightGray.copy(alpha = 0.6f)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}

@Composable
fun SkeletonItem(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
//            .padding(8.dp)
            .height(400.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Box(
                modifier = Modifier
                    .shimmerEffect()
                    .fillMaxWidth(0.6f)
                    .height(24.dp)
//                    .background(
//                        Color.LightGray.copy(alpha = 0.1f),
//                        shape = MaterialTheme.shapes.small
//                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .shimmerEffect()
                    .fillMaxWidth()
                    .height(16.dp)
//                    .background(
//                        Color.LightGray.copy(alpha = 0.1f),
//                        shape = MaterialTheme.shapes.small
//                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
//            ShimmerAnimation
            Box(
                modifier = Modifier
                    .shimmerEffect()
                    .fillMaxWidth()
                    .height(16.dp)
//                    .background(
//                        Color.LightGray.copy(alpha = 0.1f),
//                        shape = MaterialTheme.shapes.small
//                    )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .shimmerEffect()
                    .fillMaxWidth()
                    .height(300.dp)
//                    .background(
//                        Color.LightGray.copy(alpha = 0.1f),
//                        shape = MaterialTheme.shapes.small
//                    )
            )
        }
    }
}


@Composable
fun ShimmerAnimation(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        ), label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value, translateAnim.value),
        end = Offset(translateAnim.value + 200f, translateAnim.value + 200f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(brush = brush)
    )
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetailWebView(url: String, modifier: Modifier, webViewState: MutableState<WebView?>) {
    val context = LocalContext.current
//    // Compose WebView Part 9 | Removes or Stop Ad in web
    val adServers = StringBuilder()
    var line: String?
    val inputStream = context.resources.openRawResource(R.raw.adblockserverlist)
    val br = BufferedReader(InputStreamReader(inputStream))
    try {
        while (br.readLine().also { line = it } != null) {
            adServers.append(line)
            adServers.append("\n")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java", ReplaceWith("false"))
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        return false
                    }

                    override fun shouldInterceptRequest(
                        view: WebView,
                        request: WebResourceRequest
                    ): WebResourceResponse? {
                        val empty = ByteArrayInputStream("".toByteArray())
                        val kk5 = adServers.toString()
                        if (kk5.contains(":::::" + request.url.host))
                            return WebResourceResponse("text/plain", "utf-8", empty)
                        return super.shouldInterceptRequest(view, request)

                    }
                }

                settings.javaScriptEnabled = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE

                loadUrl(url)
                webViewState.value = this
            }
        },
        update = {
            it.loadUrl(url)
        },
        onRelease = {
            it.stopLoading()
            it.clearHistory()
            it.removeAllViews()
            it.destroy() // Safely destroy the WebView
            webViewState.value = null
        }
    )

}



@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    url: String,
    onBackPress: () -> Unit = {}
) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }
//    val canGoBack = remember { mutableStateOf(false) }
//    val isLoading = remember { mutableStateOf(true) }



    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    }, update = {
        it.loadUrl(url)
    })

//
//    fun loadAdBlockList(context: Context): Set<String> {
//        val inputStream = context.resources.openRawResource(R.raw.adblockserverlist)
//        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
//        val adBlockList = mutableSetOf<String>()
//        bufferedReader.forEachLine { line ->
//            adBlockList.add(line.trim()) // Trim to remove unnecessary spaces
//        }
//        return adBlockList
//    }
//
//    // Load the ad block list
//    val adBlockList = loadAdBlockList(context)


//    DisposableEffect(Unit) {
//        webView.apply {
//            settings.apply {
//                javaScriptEnabled = true
//////                domStorageEnabled = true
////                domStorageEnabled = false
////                cacheMode = WebSettings.LOAD_NO_CACHE
////                setSupportZoom(true) // Enable zoom controls
////                builtInZoomControls = true
////                displayZoomControls = false
//////                setAppCacheEnabled(true)
//            }
//
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//
//
////            webViewClient = object : WebViewClient() {
////                override fun onPageFinished(view: WebView?, url: String?) {
////                    canGoBack.value = webView.canGoBack()
////                    isLoading.value = false
////                }
////
////                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
//////                    val url = request.url?.host
//////                    return if (url != null && adBlockList.any { url.contains(it) }) {
//////                        // Block the request if the URL matches any ad domains
//////                        true
//////                    } else {
//////                        // Allow the request to proceed
//////                        false
//////                    }
//////                }
//////
//////                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
//////                    // Handle the error (show a message or error screen)
//////                }
////            }
//
////            webChromeClient = object : WebChromeClient() {
////                override fun onShowFileChooser(
////                    webView: WebView?,
////                    filePathCallback: ValueCallback<Array<Uri>>?,
////                    fileChooserParams: FileChooserParams?
////                ): Boolean {
////                    // Handle file uploads if needed
////                    return true
////                }
////            }
//            loadUrl(url)
//        }
//
//        onDispose {
//            webView.clearCache(true) // This will clear the cache completely
//            webView.stopLoading()
//            webView.clearHistory()
//            webView.removeAllViews()
//            webView.destroy()
//
//            // Force garbage collection to help reclaim memory
//            System.gc()
//
//        }
//    }

//    BackHandler(enabled = canGoBack.value) {
//        if (webView.canGoBack()) {
//            webView.goBack()
//        } else {
//            onBackPress()
//        }
//    }

//    Box(modifier = Modifier.fillMaxSize()) {
//        // Show the WebView
//        AndroidView(factory = { webView }, update = { it.loadUrl(url) },  modifier = Modifier.fillMaxSize())
//
//        // Show a CircularProgressIndicator while the page is loading
////        if (isLoading.value) {
////            CircularProgressIndicator(Modifier.align(Alignment.Center))
////        }
//    }
}




@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun HomePreview() {
    NYTTodayTheme {

        val sampleResults = listOf(
            ResultsItem(
                title = "Sample Title 1",
                abstract = "This is a sample abstract for the first item.",
                byline = "Author 1",
                url = "https://example.com/1",
                multimedia = listOf(
                    MultimediaItem(
                        url = "https://example.com/image1.jpg",
                        format = "Standard Thumbnail",
                        copyright = "Copyright 1"
                    )
                )
            ),
            ResultsItem(
                title = "Sample Title 2",
                abstract = "This is a sample abstract for the second item.",
                byline = "Author 2",
                url = "https://example.com/2",
                multimedia = listOf(
                    MultimediaItem(
                        url = "https://example.com/image2.jpg",
                        format = "Standard Thumbnail",
                        copyright = "Copyright 2"
                    )
                )
            ),
            ResultsItem(
                title = "Sample Title 3",
                abstract = "This is a sample abstract for the third item.",
                byline = "Author 3",
                url = "https://example.com/3",
                multimedia = listOf(
                    MultimediaItem(
                        url = "https://example.com/image3.jpg",
                        format = "Standard Thumbnail",
                        copyright = "Copyright 3"
                    )
                )
            )
        )


//        LoadingSkeleton()
//
        HomeContent(
            homeUiState = Resource.Loading(),
//            HomeUiState.Success(sampleResults),
            isShownBottomSheet = false,
            clickedItem = "",
            showNavigationBar = mutableStateOf(true),
            clickedItemUrl = {},
            fetchNewData = {},
            updateIsShownBottomSheetState = {},
            isRefreshing = false,
            saveArticle = {

            },
            onScrollToTop = mutableStateOf({})
        )
    }
}