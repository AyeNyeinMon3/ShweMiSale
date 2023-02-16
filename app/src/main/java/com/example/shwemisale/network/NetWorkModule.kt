package com.example.shwemisale.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.shwemi.network.UnsafeOkHttpClient
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.*
import com.example.shwemisale.repositoryImpl.AuthRepoImpl
import com.example.shwemisale.room_database.AppDatabase
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

const val BASE_URL = "http://18.141.156.213/"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
    }
    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context)  : AppDatabase {
        return AppDatabase.create(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authenticator: TokenAuthenticator,
        localDatabase: LocalDatabase,
        @ApplicationContext context: Context,
    ): OkHttpClient {
        return UnsafeOkHttpClient.unsafeOkHttpClient.apply {
            retryOnConnectionFailure(
                true)
            authenticator(authenticator)
            addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request =
                    chain.request().newBuilder()

//                        .header("Accept-Encoding", "identity")
                        .addHeader("content-type", "application/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Bearer ${localDatabase.getAccessToken()}")
                        .build()
                chain.proceed(request)
            })
            addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
            )

        }.build()
    }

    @Provides
    @Singleton
    fun provideAuthenticator(
        authRepo: Lazy<AuthRepoImpl>,
        localDatabase: LocalDatabase
    ): TokenAuthenticator {
        return TokenAuthenticator(authRepo, localDatabase)
    }

    @Provides
    @Singleton
    fun provideAuthenticationService(retrofit: Retrofit) = retrofit.create<AuthService>()

    @Provides
    @Singleton
    fun provideCustomerService(retrofit: Retrofit) = retrofit.create<CustomerService>()

    @Provides
    @Singleton
    fun provideNormalSaleService(retrofit: Retrofit) = retrofit.create<NormalSaleService>()

    @Provides
    @Singleton
    fun provideGoldFromHomeService(retrofit: Retrofit) = retrofit.create<GoldFromHomeService>()

    @Provides
    @Singleton
    fun provideCalculationService(retrofit: Retrofit) = retrofit.create<CalculationService>()

}