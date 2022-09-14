package com.fc.calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fc.calculator.model.History

@Dao
interface HistoryDao{

    @Query("select * from history")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("delete from history")
    fun deleteAll()

    /***
    @Delete
    fun delete(history: History)

    @Query("select * from history where result like :result limit 1")
    fun findByResult(result:String): History
     ***/
}
