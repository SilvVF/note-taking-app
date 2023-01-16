package io.silv.jikannoto.data.remote

import io.silv.jikannoto.data.models.NetworkChecklistItem
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.result.NotoApiResult
import io.silv.jikannoto.domain.result.NotoFetchResult
import kotlinx.coroutines.flow.Flow

interface CheckListRemoteDataSource {

    suspend fun getAllItems(): Flow<NotoFetchResult<List<NetworkChecklistItem>>>

    suspend fun deleteItemById(id: String): NotoApiResult<Nothing>

    suspend fun upsertItem(checklistItem: CheckListItem): NotoApiResult<Nothing>
}