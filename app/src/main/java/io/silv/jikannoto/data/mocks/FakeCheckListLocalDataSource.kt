package io.silv.jikannoto.data.mocks

import io.silv.jikannoto.data.local.CheckListLocalDataSource
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.models.dateTimeNow
import io.silv.jikannoto.domain.result.NotoApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class FakeCheckListLocalDataSource: CheckListLocalDataSource {

    private val db = mutableMapOf<String, CheckListItem>()

    override suspend fun getAllItems(): Flow<List<CheckListItem>> = flow {
        emit(db.values.toList())
    }

    override suspend fun getItemById(id: String): CheckListItem? {
        return db[id]
    }

    override suspend fun deleteItemById(id: String): NotoApiResult<Nothing> {
        val item = db.remove(id)
        return if (item != null)
            NotoApiResult.Success()
        else NotoApiResult.Exception(null)
    }

    override suspend fun upsertItem(name: String): NotoApiResult<Nothing> {
        db[UUID.randomUUID().toString()] = CheckListItem(
            id = UUID.randomUUID().toString(),
            dateTimeNow(),
            name,
            false
        )
        return NotoApiResult.Success()
    }
}