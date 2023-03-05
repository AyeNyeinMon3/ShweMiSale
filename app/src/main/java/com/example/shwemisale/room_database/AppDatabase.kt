
package com.example.shwemisale.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shwemisale.room_database.dao.CustomerDao
import com.example.shwemisale.room_database.dao.StockFromHomeFinalInfoDao
import com.example.shwemisale.room_database.dao.StockFromHomeInfoDao
import com.example.shwemisale.room_database.entity.CustomerEntity
import com.example.shwemisale.room_database.entity.ShweMiFileEntity
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo
import com.example.shwemisale.room_database.entity.StockFromHomeInfoEntity

@Database(
    entities = [
        CustomerEntity::class,
        StockFromHomeInfoEntity::class,
        StockFromHomeFinalInfo::class,
    ],
    version = 1
)
@TypeConverters(ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val customerDao: CustomerDao
    abstract val stockFromHomeInfoDao: StockFromHomeInfoDao
    abstract val stockFromHomeFinalInfoDao: StockFromHomeFinalInfoDao
    companion object {
        fun create(applicationContext: Context): AppDatabase {
            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "AppDatabase"
            ).allowMainThreadQueries()
                .build()
        }
    }
}