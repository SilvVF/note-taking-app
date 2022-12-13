package io.silv.jikannoto.domain.result

sealed interface NotoApiResult<T> {

    data class Exception<T>(val message: String?) : NotoApiResult<T>

    class Success<T> : NotoApiResult<T>
}

suspend fun <T> NotoApiResult<T>.onSuccess(
    execute: suspend () -> Unit
) = when (this) {
    is NotoApiResult.Success -> {
        execute()
        this
    }
    else -> this
}

suspend fun <T> NotoApiResult<T>.onException(
    execute: suspend (message: String) -> Unit
) = when (this) {
    is NotoApiResult.Exception -> {
        execute(this.message ?: "Unknown Error")
        this
    }
    else -> this
}