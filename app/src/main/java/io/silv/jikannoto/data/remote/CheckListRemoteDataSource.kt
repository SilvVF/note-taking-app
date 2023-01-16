package io.silv.jikannoto.data.remote

import io.silv.jikannoto.data.models.NetworkChecklistItem
import io.silv.jikannoto.domain.result.NotoApiResult
import kotlinx.coroutines.flow.Flow

interface CheckListRemoteDataSource {

    suspend fun getAllItems(): Flow<List<NetworkChecklistItem>>

    suspend fun getItemById(id: String): NetworkChecklistItem?

    suspend fun deleteItemById(id: String): NotoApiResult<Nothing>

    suspend fun upsertItem(name: String): NotoApiResult<Nothing>
}