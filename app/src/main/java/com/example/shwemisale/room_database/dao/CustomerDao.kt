package com.example.shwemisale.room_database.dao

import androidx.room.*
import com.example.shwemisale.room_database.entity.CustomerEntity

@Dao
interface CustomerDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCustomerData(customerData:CustomerEntity)

    @Transaction
    @Query("SELECT * FROM customer")
    fun getCustomerData():CustomerEntity

    @Query("DELETE FROM customer")
    suspend fun deleteCustomerData()
}
