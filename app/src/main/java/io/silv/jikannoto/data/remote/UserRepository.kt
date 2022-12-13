package io.silv.jikannoto.domain

import io.silv.jikannoto.domain.result.NotoApiResult

interface UserRepository : JikanNotoRepository {

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