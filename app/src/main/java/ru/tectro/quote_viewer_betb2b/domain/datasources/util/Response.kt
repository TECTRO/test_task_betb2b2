package ru.tectro.quote_viewer_betb2b.domain.datasources.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class Response<T>(open val data: T? = null, open val error: ResponseError? = null) {
    data class Success<T>(override val data: T) : Response<T>(data, null)
    data class Error<T>(override val error: ResponseError) : Response<T>(null, error)
}

sealed class FlowResponse<T>(open val data: T? = null, open val error: ResponseError? = null) {
    data class Loading<T>(val isLoading: Boolean = true, val process: Float? = null) :
        FlowResponse<T>(null, null)

    data class Success<T>(override val data: T) : FlowResponse<T>(data, null)
    data class Error<T>(override val error: ResponseError) : FlowResponse<T>(null, error)
}

data class ResponseError(
    val code: Int? = null,
    val message: ErrorMessage? = null
) {
    constructor(code: Int? = null, message: String) : this(
        code,
        ErrorMessage.StringError(message = message)
    )

    constructor(code: Int? = null, @StringRes id: Int) : this(
        code,
        ErrorMessage.ResourceError(id)
    )
}

sealed class ErrorMessage {
    data class StringError(val message: String) : ErrorMessage()
    data class ResourceError(@StringRes val id: Int) : ErrorMessage()

    @Composable
    fun getString(): String = when (this) {
        is ResourceError -> stringResource(id = id)
        is StringError -> message
    }

    fun getString(context: Context): String = when (this) {
        is ResourceError -> context.applicationContext.getString(id)
        is StringError -> message
    }
}