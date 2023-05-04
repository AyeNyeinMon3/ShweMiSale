package com.example.shwemisale.repositoryImpl

import com.example.shwemi.util.Resource
import com.example.shwemi.util.getErrorMessageFromHashMap
import com.example.shwemi.util.parseError
import com.example.shwemi.util.parseErrorWithDataClass
import com.example.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.example.shwemisale.data_layers.domain.customers.CustomerWhistListDomain
import com.example.shwemisale.data_layers.dto.SimpleError
import com.example.shwemisale.data_layers.dto.SimpleResponse
import com.example.shwemisale.data_layers.dto.auth.AuthError
import com.example.shwemisale.data_layers.dto.customers.*
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.CustomerService
import com.example.shwemisale.repository.CustomerRepository
import javax.inject.Inject

class CustomerRepoImpl @Inject constructor(
    private val customerService: CustomerService,
    private val localDatabase: LocalDatabase
) : CustomerRepository {


    override suspend fun getCustomerDataByCode(
        code:String?,
        name: String?,
        phone: String?,
        date_of_birth: String?,
        gender: String?,
        province_id: String?,
        township_id: String?,
        address: String?,
        nrc: String?
    ): Resource<CustomerDataResponse> {
        return try {
            val response = customerService.searchCustomerData(
                localDatabase.getAccessToken().orEmpty(),
                code,
                name,
                phone,
                date_of_birth,
                gender,
                province_id,
                township_id,
                address,
                nrc
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                 val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getCustomerWhistList(customerId: String): Resource<List<CustomerWhistListDomain>> {
        return try {
            val response = customerService.getCustomerWhistList(
                localDatabase.getAccessToken().orEmpty(),
                customerId
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.map { it.asDomain() })
            } else {
                 val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getProvince(): Resource<List<ProvinceDto>> {
        return try {
            val response = customerService.getProvince(
                localDatabase.getAccessToken().orEmpty(),
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else {
                 val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getTownship(province_id: String): Resource<List<TownshipDto>> {
        return try {
            val response = customerService.getTownship(
                localDatabase.getAccessToken().orEmpty(),
                province_id
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data)
            } else {
                 val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addNewUser(
        name: String,
        phone: String,
        date_of_birth: String,
        gender: String,
        province_id: String,
        township_id: String,
        address: String,
        nrc: String
    ):Resource<CustomerDataDomain> {
        return try {
            val response = customerService.addNewUser(
                localDatabase.getAccessToken().orEmpty(),
                name, phone, date_of_birth, gender, province_id, township_id, address, nrc
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.asDomain())
            } else {
                val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}