package com.example.nyttoday.data

import kotlinx.serialization.Serializable


@Serializable
data class NYTResponse(
	val results: List<ResultsItem?>? = emptyList(),
	val numResults: Int? = 0,
	val status: String? = ""
)

@Serializable
data class MultimediaItem(
	val copyright: String? = "",
	val format: String? = "",
	val url: String? = ""
)

@Serializable
data class ResultsItem(
	val title: String? = "",
	val abstract: String? = "",
	val desFacet: List<String?>? = emptyList(),
	val uri: String? = "",
	val url: String? = "",
	val shortUrl: String? = "",
	val multimedia: List<MultimediaItem?>? = emptyList(),
	val updatedDate: String? = "",
	val createdDate: String? = "",
	val byline: String? = ""
)
