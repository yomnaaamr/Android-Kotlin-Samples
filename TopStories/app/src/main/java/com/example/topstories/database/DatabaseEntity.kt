package com.example.topstories.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.topstories.domain.AppNewsModel


@Entity(tableName = "news")
data class DatabaseNews (
    val title : String?,
    @ColumnInfo(name = "sub_title") val subTitle : String?,
    @PrimaryKey val url : String,
    @ColumnInfo(name = "by_line") val byLine : String?,
    @ColumnInfo(name = "updated_date") val updatedDate : String?,
    @ColumnInfo(name = "image_url") val imageUrl : String?,
    @ColumnInfo(name = "copy_right") val copyRight : String?
)


/**
 * Map DatabaseVideos to domain entities.
 */
fun List<DatabaseNews>.asDomainModel() : List<AppNewsModel>{

    return map {
            AppNewsModel(
                   title = it.title,
                   subTitle = it.subTitle,
                   url = it.url,
                   byLine = it.byLine,
                   updatedDate = it.updatedDate,
                   imageUrl = it.imageUrl,
                   copyRight = it.copyRight
            )
    }
}