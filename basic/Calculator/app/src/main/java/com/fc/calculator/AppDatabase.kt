package com.fc.calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fc.calculator.dao.HistoryDao
import com.fc.calculator.model.History

@Database(entities=[History::class], version=1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}