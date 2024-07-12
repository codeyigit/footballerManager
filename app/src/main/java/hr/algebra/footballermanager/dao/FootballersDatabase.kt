package hr.algebra.footballermanager.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Footballer::class],version =1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FootballersDatabase : RoomDatabase(){
    abstract fun footballerDao(): FootballerDao
    companion object {
        @Volatile
        private var INSTANCE: FootballersDatabase? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(FootballersDatabase::class.java) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }


        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            FootballersDatabase::class.java,
            "footballers.db"
        ).build()
    }
}