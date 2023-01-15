package io.silv.jikannoto.data.remote

import io.silv.jikannoto.domain.JikanNotoRepository
import io.silv.jikannoto.domain.models.User
import io.silv.jikannoto.domain.result.NotoApiResult
import kotlinx.coroutines.flow.Flow

interface UserRepository : JikanNotoRepository {

    suspend fun sendPasswordReset(email: String): NotoApiResult<Nothing>
    suspend fun currentUserInfo(): Flow<User>
    suspend fun login(
        username: String,
        password: String
    ): NotoApiResult<Nothing>

    suspend fun logout(): NotoApiResult<Nothing>

    suspend fun register(
        username: String,
        password: String
    ): NotoApiResult<Nothing>
}