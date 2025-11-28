package ca.unb.mobiledev.appdevproject.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.unb.mobiledev.appdevproject.dao.ProductDao
import ca.unb.mobiledev.appdevproject.entities.Product
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ProductDao(): ProductDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "loadingbay"
                ).createFromAsset("loadingbay.db")
                    .build()

                Log.i("database", "loaded")
                INSTANCE = instance
                return instance
            }
        }
    }
}