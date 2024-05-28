package jp.co.rakuten.ticket.checkinstation.di

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.co.rakuten.ticket.checkinstation.api.ApiHandlingCallAdapterFactory
import jp.co.rakuten.ticket.checkinstation.api.ApiManager
import jp.co.rakuten.ticket.checkinstation.api.ApiService
import jp.co.rakuten.ticket.checkinstation.api.repository.ApiRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @Singleton
    fun provideInterceptor(@ApplicationContext context: Context): Interceptor = ApiManager.createInterceptor(context)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = ApiManager.createHttpLoggingInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        ApiManager.createOkHttpClient(interceptor, loggingInterceptor)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = ApiManager.createMoshi()

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        ApiManager.createMoshiConverterFactory(moshi)

    @Provides
    @Singleton
    fun provideApiHandlingCallAdapterFactory(): ApiHandlingCallAdapterFactory =
        ApiManager.createApiHandlingCallAdapterFactory()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient,
                        moshiConverterFactory: MoshiConverterFactory,
                        apiHandlingCallAdapterFactory: ApiHandlingCallAdapterFactory
    ): Retrofit =
        ApiManager.createRetrofit(okHttpClient, moshiConverterFactory, apiHandlingCallAdapterFactory)

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = ApiManager.createApiService(retrofit)

    @Provides
    @Singleton
    fun providesApiRepository(apiService: ApiService): ApiRepository = ApiRepository(apiService)
}