package com.example.nyttoday.data

import retrofit2.Response
import retrofit2.http.GET


interface NewsApi {

    @GET("topstories/v2/home.json")
    suspend fun getTopStories(): Response<NYTResponse>

//    @GET("topstories/v2/home.json")
//    suspend fun getStoryByUrl(@Query("url") url: String): ResultsItem

//    @GET("news/v3/content/all/all.json")
//    suspend fun getNewsByPaging(
//        @Query("limit") limit: Int,
//        @Query("offset") offset: Int
//    ): NYTResponse



//    https://api.nytimes.com/svc/news/v3/content/all/all.json?qf={limit%20=%2040,offset%20=%200}&api-key=DBrGZBTi4MVRtFbaUuey8EUvkAwTsziR




//    @GET("search/v2/articlesearch.json")
//    suspend fun search(@Query("q") query: String): NYTResponse



    /**
    https://api.nytimes.com/svc/search/v2/articlesearch.json?
    qf={web_url%20=%20https://www.nytimes.com/2024/08/15/business/economy/kamala-harris-inflation-price-gouging.html}&api-key=DBrGZBTi4MVRtFbaUuey8EUvkAwTsziR
     *
     */
}