package io.silv.jikannoto.domain.result

sealed class NotoFetchResult<T> : NotoApiResult<T> {

    data class Success<T>(val data: T) : NotoFetchResult<T>()

    class Empty<T> : NotoFetchResult<T>()
}

suspend fun <T> NotoFetchResult<T>.onSuccess(
    execute: suspend (data: T) -> Unit
): NotoFetchResult<T> = when (this) {
    is NotoFetchResult.Success -> {
        execute(this.data)
        this
    }
    else -> this
}

suspend fun <T> NotoFetchResult<T>.onEmpty(
    execute: suspend () -> Unit
): NotoFetchResult<T> = when (this) {
    is NotoFetchResult.Empty -> {
        execute()
        this
    }
    else -> this
}