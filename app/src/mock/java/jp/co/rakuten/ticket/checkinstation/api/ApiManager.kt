package jp.co.rakuten.ticket.checkinstation.api

import android.content.Context
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.internal.Contexts
import jp.co.rakuten.ticket.checkinstation.BuildConfig
import jp.co.rakuten.ticket.checkinstation.MyApplication
import jp.co.rakuten.ticket.checkinstation.util.LoginUtil
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.FileNotFoundException
import java.util.*
import java.util.concurrent.TimeUnit

object ApiManager {

    private const val CONNECTION_TIMEOUT = 30L

    fun createApiService(retrofit: Retrofit): ApiService = MockApiService()

    fun createRetrofit(okHttpClient: OkHttpClient,
                       moshiConverterFactory: MoshiConverterFactory,
                       apiHandlingCallAdapterFactory: ApiHandlingCallAdapterFactory
    ): Retrofit
            = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .client(okHttpClient)
        .addConverterFactory(moshiConverterFactory)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    fun createMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .build()

    fun createMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory = MoshiConverterFactory.create(moshi)

    fun createApiHandlingCallAdapterFactory(): ApiHandlingCallAdapterFactory = ApiHandlingCallAdapterFactory.create()

    fun createOkHttpClient(interceptor: Interceptor, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(loggingInterceptor)
        }
        return clientBuilder.build()
    }

    fun createInterceptor(context: Context): Interceptor = Interceptor { chain ->
        val original = chain.request()

        val url = original.url.newBuilder().build()

        val app = Contexts.getApplication(context) as MyApplication

        val builder = original.newBuilder()
            .header("Accept", "application/json")
            .method(original.method, original.body)
            .url(url)

        if (original.url.toString().contains(BuildConfig.API_URL) && LoginUtil.getInstance().getToken().isNotEmpty()) {
            builder.addHeader("token", LoginUtil.getInstance().getToken())
        }

        val code: Int
        val rawBody: String?
        val response = chain.proceed(builder.build())
        val responseBody = response.body
        code = response.code
        rawBody = responseBody?.string()
        //E@: error
        if (code != 200) {
            var message = ""
            if (rawBody!=null){
                message = rawBody.split("E@:").get(1).split("\n").get(0)
            }
            return@Interceptor response.newBuilder()
                .message(message)
                .code(code)
                .body(rawBody?.toResponseBody(responseBody?.contentType())).build()
        }
        return@Interceptor response.newBuilder()
            .code(code)
            .body(rawBody?.toResponseBody(responseBody?.contentType())).build()
    }

    fun createHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY }
}