package com.example.shwemisale.repositoryImpl

import com.example.shwemi.util.Resource
import com.example.shwemi.util.getErrorMessageFromHashMap
import com.example.shwemi.util.parseError
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
                Resource.Success(response.body()!!.data!!.name)
            } else {
                val errorMessage =
                    response.errorBody()?.parseError<AuthError>()?.message
                if (errorMessage.isNullOrEmpty()){
                    val errorMessageWithMap =
                        response.errorBody()?.parseError()

                    Resource.Error(getErrorMessageFromHashMap(errorMessageWithMap!!))
                }else if (response.code() == 500) {
                    Resource.Error("Server Error")
                }else{
                    Resource.Error(errorMessage)
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}