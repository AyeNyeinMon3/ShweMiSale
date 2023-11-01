
package com.shwemigoldshop.shwemisale.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shwemigoldshop.shwemisale.room_database.dao.CustomerDao
import com.shwemigoldshop.shwemisale.room_database.dao.SampleDao
import com.shwemigoldshop.shwemisale.room_database.dao.StockFromHomeFinalInfoDao
import com.shwemigoldshop.shwemisale.room_database.dao.StockFromHomeInfoDao
import com.shwemigoldshop.shwemisale.room_database.entity.*

@Database(
    entities = [
        CustomerEntity::class,
        StockFromHomeInfoEntity::class,
        StockFromHomeFinalInfo::class,
        SampleEntity::class,
    ],
    version = 1
)
@TypeConverters(ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val customerDao: CustomerDao
    abstract val stockFromHomeInfoDao: StockFromHomeInfoDao
    abstract val stockFromHomeFinalInfoDao: StockFromHomeFinalInfoDao
    abstract val sampleDao: SampleDao
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