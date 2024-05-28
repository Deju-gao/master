package jp.co.rakuten.ticket.checkinstation.api

import jp.co.rakuten.ticket.checkinstation.api.error.ApiErrorException
import jp.co.rakuten.ticket.checkinstation.api.error.HttpErrorException
import retrofit2.*
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiHandlingCallAdapterFactory private constructor(): CallAdapter.Factory() {
    companion object {
        fun create() = ApiHandlingCallAdapterFactory()
    }

    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }
        require(returnType is ParameterizedType) { "Call return type must be parameterized as Call<Response> or Call<? extends Response>" }
        return Adapter<Any>(getParameterUpperBound(0, returnType))
    }

    private class Adapter<T>(private val responseType: Type): CallAdapter<T, Call<T>> {
        override fun responseType(): Type = responseType
        override fun adapt(call: Call<T>): Call<T> = ErrorHandlingCall(call)
    }
}

class ErrorHandlingCall<T>(private val delegate: Call<T>): Call<T> by delegate {
    override fun enqueue(callback: Callback<T>) =
        delegate.enqueue(object: Callback<T> by callback {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    callback.onResponse(this@ErrorHandlingCall, response)
                    return
                }

                response.errorBody()?.let { errorBody ->
                    if (errorBody.contentLength() == 0L) {
                        callback.onResponse(this@ErrorHandlingCall, response)
                        return
                    }

                    try {
                        callback.onFailure(this@ErrorHandlingCall, ApiErrorException())
                    } catch (e: Exception) {
                        callback.onResponse(this@ErrorHandlingCall, response)
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onFailure(this@ErrorHandlingCall, HttpErrorException())
            }
        })
}