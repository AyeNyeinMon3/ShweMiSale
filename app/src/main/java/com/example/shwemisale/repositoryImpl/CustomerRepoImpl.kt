package com.example.shwemisale.repositoryImpl

import com.example.shwemi.util.Resource
import com.example.shwemi.util.getErrorMessageFromHashMap
import com.example.shwemi.util.parseError
import com.example.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.example.shwemisale.data_layers.domain.customers.CustomerWhistListDomain
import com.example.shwemisale.data_layers.dto.auth.AuthError
import com.example.shwemisale.data_layers.dto.customers.asDomain
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.CustomerService
import com.example.shwemisale.repository.CustomerRepository
import javax.inject.Inject

class CustomerRepoImpl @Inject constructor(
    private val customerService: CustomerService,
    private val localDatabase: LocalDatabase
):CustomerRepository {
    override suspend fun getCustomerDataByCode(customerCode: String): Resource<CustomerDataDomain> {
        return try {
            val response = customerService.getCustomerDataByCode(localDatabase.getAccessToken().orEmpty(),customerCode)

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.asDomain())
            } else {
                val errorMessage =
                    response.errorBody()?.parseError<AuthError>()?.message
                if (errorMessage.isNullOrEmpty()){
                    val errorMessageWithMap =
                        response.errorBody()?.parseError()

                    Resource.Error(getErrorMessageFromHashMap(errorMessageWithMap!!))
                }else{
                    Resource.Error(errorMessage)
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getCustomerWhistList(customerId: String): Resource<List<CustomerWhistListDomain>> {
        return try {
            val response = customerService.getCustomerWhistList(localDatabase.getAccessToken().orEmpty(),customerId)

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.map { it.asDomain() })
            } else {
                val errorMessage =
                    response.errorBody()?.parseError<AuthError>()?.message
                if (errorMessage.isNullOrEmpty()){
                    val errorMessageWithMap =
                        response.errorBody()?.parseError()

                    Resource.Error(getErrorMessageFromHashMap(errorMessageWithMap!!))
                }else{
                    Resource.Error(errorMessage)
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}