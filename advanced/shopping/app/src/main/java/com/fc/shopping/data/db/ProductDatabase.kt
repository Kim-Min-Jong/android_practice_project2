package com.fc.shopping.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fc.shopping.data.db.dao.ProductDao
import com.fc.shopping.data.entity.product.ProductEntity
import com.fc.shopping.utility.DateConverter

@Database(entities=[ProductEntity::class], version = 1, exportSchema = false)
// 받는 Date 타입을 온전하게 받기위해 컨버터 추가
@TypeConverters(DateConverter::class)
abstract class ProductDatabase: RoomDatabase() {
    companion object {
        const val DB_NAME = "ProductDataBase.db"
    }
    abstract fun productDao(): ProductDao
}