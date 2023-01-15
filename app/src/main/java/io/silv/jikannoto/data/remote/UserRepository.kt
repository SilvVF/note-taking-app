package io.silv.jikannoto.data.remote

import io.silv.jikannoto.domain.JikanNotoRepository
import io.silv.jikannoto.domain.models.User
import io.silv.jikannoto.domain.result.NotoApiResult

interface UserRepository : JikanNotoRepository {

    suspend fun sendPasswordReset(email: String): NotoApiResult<Nothing>
    suspend fun getUserInfo(): User
    suspend fun login(
        username: String,
        password: String
    ): NotoApiResult<Nothing>

    suspend fun logout(
        username: String,
        password: String
    ): NotoApiResult<Nothing>

    suspend fun register(
        username: String,
        password: String
    ): NotoApiResult<Nothing>
}