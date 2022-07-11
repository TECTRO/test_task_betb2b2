package ru.tectro.quote_viewer_betb2b.domain.datasources.repo

import android.util.Log
import retrofit2.HttpException
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.Response
import ru.tectro.quote_viewer_betb2b.domain.datasources.util.ResponseError

suspend fun <T> networkCall(call: suspend () -> T): Response<T> {
    return try {
        Response.Success(call())
    } catch (e: HttpException) {
        Log.e("NETWORK", e.stackTraceToString())
        Response.Error(ResponseError(e.code(), e.message()))
    } catch (e: Exception) {
        Log.e("NETWORK", e.stackTraceToString())
        Response.Error(ResponseError(message = e.message ?: "unknown error"))
    }
}