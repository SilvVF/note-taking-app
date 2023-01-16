package io.silv.jikannoto.data.local

import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.result.NotoApiResult
import kotlinx.coroutines.flow.Flow

interface CheckListLocalDataSource {

    suspend fun getAllItems(): Flow<List<CheckListItem>>

    suspend fun getItemById(id: String): CheckListItem?

    suspend fun deleteItemById(id: String): NotoApiResult<Nothing>

    suspend fun upsertItem(name: String): NotoApiResult<Nothing>
}
