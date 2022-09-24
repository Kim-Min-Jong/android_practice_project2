package com.fc.bookreview

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fc.bookreview.dao.HistoryDao
import com.fc.bookreview.model.History

@Database(entities=[History::class], version=1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}