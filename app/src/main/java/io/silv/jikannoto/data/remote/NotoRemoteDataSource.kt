package io.silv.jikannoto.data.remote

import io.silv.jikannoto.data.models.NetworkNoto
import io.silv.jikannoto.domain.JikanNotoRepository
import io.silv.jikannoto.domain.result.NotoApiResult
import io.silv.jikannoto.domain.result.NotoFetchResult

interface NotoRemoteDataSource : JikanNotoRepository {

    suspend fun deleteNoto(id: String): NotoApiResult<Nothing>

    suspend fun upsertNoto(noto: NetworkNoto): NotoApiResult<Nothing>

    suspend fun fetchAllNotos(owner: String): NotoFetchResult<List<NetworkNoto>>
}