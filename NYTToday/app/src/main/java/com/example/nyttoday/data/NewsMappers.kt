package com.example.nyttoday.data

import com.example.nyttoday.firebase.FirebaseNewItem

fun ResultsItem.toFirebaseNewItem(): FirebaseNewItem {
    return FirebaseNewItem(
        title = this.title,
        abstract = this.abstract,
        uri = this.uri,
        url = this.url,
        byline = this.byline,
        imageUrl = this.multimedia?.getOrNull(0)?.url,
        imageCopyRight = this.multimedia?.getOrNull(0)?.copyright
    )
}


fun FirebaseNewItem.toArticle(): Article {
    return Article(
        title = this.title,
        abstract = this.abstract,
        uri = this.uri,
        url = this.url,
        byline = this.byline,
        imageUrl = this.imageUrl,
        copyright = this.imageCopyRight
    )
}

//fun FirebaseNewItem.toResultItem(): ResultsItem {
//    val multimediaList = if (this.imageUrl != null && this.imageCopyRight != null) {
//        listOf(
//            MultimediaItem(
//                url = this.imageUrl,
//                copyright = this.imageCopyRight,
//            )
//        )
//    } else {
//        emptyList()
//    }
//
//    return ResultsItem(
//        title = this.title,
//        abstract = this.abstract,
//        uri = this.uri,
//        url = this.url,
//        byline = this.byline,
//        multimedia = multimediaList
//    )
//}