package io.silv.jikannoto.data.local

import io.silv.jikannoto.domain.result.NotoApiResult
import jikannoto.notodb.CheckListEntity
import kotlinx.coroutines.flow.Flow

interface CheckListLocalDataSource {

    suspend fun getAllItems(): Flow<List<CheckListEntity>>

    suspend fun getItemById(id: String): CheckListEntity?

    suspend fun deleteItemById(id: String): NotoApiResult<Nothing>

    suspend fun upsertItem(name: String): NotoApiResult<Nothing>
}
