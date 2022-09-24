package com.fc.bookreview

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fc.bookreview.dao.HistoryDao
import com.fc.bookreview.dao.ReviewDao
import com.fc.bookreview.model.History
import com.fc.bookreview.model.Review

@Database(entities=[History::class, Review::class], version=1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}
fun getAppDatabase(context: Context): AppDatabase {

    val migration_1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE `REVIEW` (`id` INTEGER, `review` TEXT," + "PRIMARY KEY(`id`))")
        }

    }
    return Room.databaseBuilder(context, AppDatabase::class.java, "BookSearchDB")
//        .fallbackToDestructiveMigration().build()
        .addMigrations(migration_1_2).build()
}
