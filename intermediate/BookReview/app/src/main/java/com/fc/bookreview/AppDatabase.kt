package com.fc.bookreview

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fc.bookreview.dao.HistoryDao
import com.fc.bookreview.dao.ReviewDao
import com.fc.bookreview.model.History
import com.fc.bookreview.model.Review

@Database(entities=[History::class, Review::class], version=1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}