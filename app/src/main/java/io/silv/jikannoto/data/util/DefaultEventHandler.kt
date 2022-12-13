package io.silv.jikannoto.data.util

import com.google.android.gms.tasks.Task
import io.silv.jikannoto.domain.result.NotoApiResult
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@Suppress("UNCHECKED_CAST")
internal fun <V, T : NotoApiResult<V>, TResult> Task<TResult>.defaultEventHandles(
    continuation: Continuation<T>,
) = this
    .addOnSuccessListener {
        continuation.resume(
            value = NotoApiResult.Success<V>() as T,
        )
    }
    .addOnFailureListener { exception ->
        continuation.resume(
            value = NotoApiResult.Exception<V>(exception.message) as T,
        )
    }

inline fun <T> safeCall(action: () -> NotoApiResult<T> = { NotoApiResult.Success() }): NotoApiResult<T> {
    return try {
        action()
    } catch (e: Exception) {
        NotoApiResult.Exception(e.message ?: "An unknown Error Occurred")
    }
}

fun <T> onNoExceptionThrown(result: () -> T) = result()