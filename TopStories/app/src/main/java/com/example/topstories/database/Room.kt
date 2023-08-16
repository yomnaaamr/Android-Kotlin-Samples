package com.example.topstories.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.topstories.util.getDateToday

@Dao
interface NewsDao{

    @Query("DELETE FROM news WHERE updated_date < :today")
    fun deleteNewsBeforeToday(today : String = getDateToday())

    @Query("SELECT * FROM news")
    fun getNews() : LiveData<List<DatabaseNews>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(news: List<DatabaseNews>?)

}



@Database(entities = [DatabaseNews::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao() : NewsDao

    companion object{
        @Volatile
        private  var INSTANCE : NewsDatabase? = null

        fun getDatabase(context: Context) : NewsDatabase{
           return INSTANCE ?: synchronized(this){
               val instance = Room.databaseBuilder(
                   context,
                   NewsDatabase::class.java,
                   "news"
               ).build()
               INSTANCE = instance

               instance
           }

        }
    }
}




