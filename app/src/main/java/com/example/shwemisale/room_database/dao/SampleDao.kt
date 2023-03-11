package com.example.shwemisale.room_database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.shwemisale.room_database.entity.SampleEntity
import com.example.shwemisale.room_database.entity.StockFromHomeFinalInfo

@Dao
interface SampleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSample(sampleEntity: SampleEntity)

    @Update
    suspend fun updateSample(sampleEntity: SampleEntity)

    @Transaction
    @Query("SELECT * FROM samples")
    fun getSamples(): LiveData<List<SampleEntity>>

    @Delete
    suspend fun deleteSamples(sample:SampleEntity)
}