package com.example.shwemisale.network

import android.util.Log
import com.example.shwemi.util.Resource
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.repositoryImpl.AuthRepoImpl
import dagger.Lazy
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val authRepo: Lazy<AuthRepoImpl>,
    private val localDatabase: LocalDatabase
) : Authenticator {

    var result: Request? = null
    private fun getNewAccessToken(): Resource<String> {
        return authRepo.get().getNewAccessToken()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.body?.contentLength() == 103L) {
            return null
        } else {
            when(val refreshToken = getNewAccessToken()){
                is Resource.Loading->{

                }
                is Resource.Success->{
                    localDatabase.saveToken(refreshToken.data.orEmpty())
                    result = response.request.newBuilder()
                        .header("Authorization", localDatabase.getAccessToken().orEmpty())
                        .build().also {
                            Log.i("new_request", "new req url => ${it.url}, new req header size => ${it.headers.size}, new token => ${refreshToken.data}")
                        }
                }
                is Resource.Error->{
                    result = null
                }
            }
            return result

        }
    }

}
