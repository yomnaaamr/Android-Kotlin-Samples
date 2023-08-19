package com.example.topstories.network

import com.example.topstories.database.DatabaseNews
import com.example.topstories.domain.AppNewsModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response(
	@Json(name="results")
	val results: List<ResultsItem?>? = null,
)


fun Response.asDomainModel() : List<AppNewsModel>? {
	return results?.map {
		AppNewsModel(
             title = it?.title,
			 subTitle = it?.jsonMemberAbstract,
			 url = it!!.url,
			 byLine = it.byline,
			 updatedDate = it.updatedDate,
			 imageUrl = it.multimedia?.get(0)?.url,
			 copyRight = it.multimedia?.get(0)?.copyright

		)
	}

}


fun Response.asDatabaseModel() : List<DatabaseNews>?{
	return results?.map {
		DatabaseNews(
             title = it?.title,
			 subTitle = it?.jsonMemberAbstract,
			 url = it!!.url,
			byLine = it.byline,
			updatedDate = it.updatedDate,
			imageUrl = it.multimedia?.get(0)?.url,
			copyRight = it.multimedia?.get(0)?.copyright
		)
	}
}

@JsonClass(generateAdapter = true)
data class ResultsItem(

	@Json(name="abstract")
	val jsonMemberAbstract: String? = null,

	@Json(name="title")
	val title: String? = null,

	@Json(name="url")
	val url: String,

	@Json(name="multimedia")
	val multimedia: List<MultimediaItem?>? = null,

	@Json(name="created_date")
	val updatedDate: String? = null,

	@Json(name="byline")
	val byline: String? = null,

)

@JsonClass(generateAdapter = true)
data class MultimediaItem(

	@Json(name="copyright")
	val copyright: String? = null,

	@Json(name="url")
	val url: String? = null,

)
