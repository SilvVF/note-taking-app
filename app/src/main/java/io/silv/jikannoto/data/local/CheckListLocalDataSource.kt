package io.silv.jikannoto.data.local

import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.result.NotoApiResult
import jikannoto.notodb.CheckListEntity
import kotlinx.coroutines.flow.Flow

interface CheckListLocalDataSource {

    fun getAllItems(): Flow<List<CheckListEntity>>

    suspend fun getItemById(id: String): CheckListEntity?

    suspend fun deleteItemById(id: String): NotoApiResult<Nothing>

    suspend fun upsertItem(checkListItem: CheckListItem): NotoApiResult<Nothing>

    suspend fun addLocallyDeleted(id: String)

    suspend fun removeLocallyDeleted(id: String)

    suspend fun getAllLocallyDeletedIds(): List<String>
}
