package com.shwemigoldshop.shwemisale.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.shwemigoldshop.shwemisale.localDataBase.LocalDatabase
import com.shwemigoldshop.shwemisale.network.api_services.*
import com.shwemigoldshop.shwemisale.repositoryImpl.AuthRepoImpl
import com.shwemigoldshop.shwemisale.room_database.AppDatabase
import com.squareup.moshi.Moshi
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

const val BASE_URL = "https://sales.shwemigoldshop.com/"
const val BASE_URL_STG_I = "https://stg1sales.shwemigoldshop.com/"
const val BASE_URL_STG_II = "https://stg2sales.shwemigoldshop.com/"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(LenientJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL_STG_II)
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
        return OkHttpClient.Builder().apply {
            retryOnConnectionFailure(
                true)
            authenticator(authenticator)
            addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request =

                    chain.request().newBuilder()

//                        .header("Accept-Encoding", "identity")
                        .addHeader("content-type", "application/json")
                        .addHeader("Accept", "application/json")
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

    @Provides
    @Singleton
    fun provideProductService(retrofit: Retrofit) = retrofit.create<ProductService>()

    @Provides
    @Singleton
    fun providePawnService(retrofit: Retrofit) = retrofit.create<PawnService>()

    @Provides
    @Singleton
    fun providePrintingService(retrofit: Retrofit) = retrofit.create<PrintingService>()

}