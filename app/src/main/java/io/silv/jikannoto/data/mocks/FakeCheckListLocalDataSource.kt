package io.silv.jikannoto.data.mocks

import io.silv.jikannoto.data.local.CheckListLocalDataSource
import io.silv.jikannoto.domain.result.NotoApiResult
import java.util.UUID
import jikannoto.notodb.CheckListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCheckListLocalDataSource : CheckListLocalDataSource {

    private val db = mutableMapOf<String, CheckListEntity>()

    override suspend fun getAllItems(): Flow<List<CheckListEntity>> = flow {
        emit(db.values.toList())
    }

    override suspend fun getItemById(id: String): CheckListEntity? {
        return db[id]
    }

    override suspend fun deleteItemById(id: String): NotoApiResult<Nothing> {
        val item = db.remove(id)
        return if (item != null)
            NotoApiResult.Success()
        else NotoApiResult.Exception(null)
    }

    override suspend fun upsertItem(name: String): NotoApiResult<Nothing> {
        db[UUID.randomUUID().toString()] = CheckListEntity(
            id = UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            name,
            false
        )
        return NotoApiResult.Success()
    }
}