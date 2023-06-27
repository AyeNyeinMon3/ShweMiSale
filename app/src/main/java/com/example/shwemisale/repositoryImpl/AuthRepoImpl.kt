package com.example.shwemisale.repositoryImpl

import com.example.shwemi.network.dto.ResponseDto
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getErrorMessageFromHashMap
import com.example.shwemi.util.parseError
import com.example.shwemi.util.parseErrorWithDataClass
import com.example.shwemisale.data_layers.dto.SimpleError
import com.example.shwemisale.data_layers.dto.auth.AuthError
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.AuthService
import com.example.shwemisale.repository.AuthRepository
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authService: AuthService,
    private val localDatabase: LocalDatabase
) : AuthRepository {
    override fun getNewAccessToken(): Resource<String> {
        return try {
            val responseCall = authService.refreshToken(localDatabase.getAccessToken().orEmpty())
            val response = responseCall.execute()
            if (response.body() != null) {

                Resource.Success(response.body()!!.data!!.token)
            } else {
                Resource.Error(response.errorBody()?.string().orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override fun refreshTokenLog(): Resource<String> {
        return try {
            val responseCall = authService.refreshTokenLog(localDatabase.getAccessToken().orEmpty())
            val response = responseCall.execute()
            if (response.body() != null) {
                Resource.Success(response.body()!!.data!!.token)
            } else {
                Resource.Error(response.errorBody()?.string().orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun login(userName: String, password: String): Resource<String> {
        return try {
            val response = authService.login(userName, password)
            if (response.isSuccessful && response.body() != null) {
                localDatabase.saveToken(response.body()!!.data!!.token.orEmpty())
                Resource.Success(response.body()!!.data!!.token)
            } else {
                Resource.Error(response.errorBody()!!.string())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }

    }

    override suspend fun logout(): Resource<String> {
        return try {
            val response = authService.logout(localDatabase.getAccessToken().orEmpty())
            if (response.isSuccessful && response.body() != null) {
                localDatabase.removeToken()
                localDatabase.clearSharedPreference()
                Resource.Success(response.body()!!.response.message)
            } else {
                Resource.Error(response.errorBody()!!.string())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }


    override suspend fun getProfile(): Resource<String> {
        return try {
            val response = authService.getProfile(localDatabase.getAccessToken().orEmpty())

            if (response.isSuccessful && response.body() != null) {
                localDatabase.saveCurrentSalesPersonName(response.body()?.data?.name.orEmpty())
                Resource.Success(response.body()!!.data!!.name)
            } else if (response.code() == 500) {
                Resource.Error("500 Server Error")
            } else {
                val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<ResponseDto>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.message)
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