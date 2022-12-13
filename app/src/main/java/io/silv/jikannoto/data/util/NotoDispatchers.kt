package io.silv.jikannoto.data.util

import kotlinx.coroutines.CoroutineDispatcher

interface NotoDispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}