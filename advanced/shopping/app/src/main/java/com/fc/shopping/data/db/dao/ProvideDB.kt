package com.fc.shopping.data.db.dao

import android.content.Context
import androidx.room.Room
import com.fc.shopping.data.db.ProductDatabase

internal fun provideDB(context: Context): ProductDatabase =
    Room.databaseBuilder(context, ProductDatabase::class.java, ProductDatabase.DB_NAME).build()

internal fun provideToDoDao(database: ProductDatabase) = database.productDao()