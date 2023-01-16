package io.silv.jikannoto.data.local

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.silv.jikannoto.NotoDatabase
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.result.NotoApiResult
import java.time.ZoneOffset
import jikannoto.notodb.CheckListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.toJavaLocalDateTime

class CheckListLocalDataSourceImpl(
    db: NotoDatabase,
    private val dispatcher: NotoDispatchers
) : CheckListLocalDataSource {

    private val db = db.checkListEntityQueries

    override fun getAllItems(): Flow<List<CheckListEntity>> = db.getAllCheckListEntitys().asFlow().mapToList()

    override suspend fun getItemById(id: String): CheckListEntity? = withContext(dispatcher.io) {
        db.getCheckListEntityById(id).executeAsOneOrNull()
    }

    override suspend fun deleteItemById(id: String): NotoApiResult<Nothing> = withContext(dispatcher.io) {
        kotlin.runCatching {
            db.deleteCheckListEntityById(id)
        }.onSuccess {
            return@withContext NotoApiResult.Success<Nothing>()
        }
        NotoApiResult.Exception("failed to delete node with id: $id")
    }

    override suspend fun upsertItem(checkListItem: CheckListItem): NotoApiResult<Nothing> = withContext(dispatcher.io) {
        db.upsertCheckListEntity(
            checkListItem.id,
            checkListItem.dateCreated.toJavaLocalDateTime().toEpochSecond(ZoneOffset.UTC) * 1000,
            checkListItem.name,
            checkListItem.completed
        )
        NotoApiResult.Success()
    }

    override suspend fun addLocallyDeleted(id: String) {
        db.addLocallyDeletedCheckListEntity(id)
    }

    override suspend fun removeLocallyDeleted(id: String) {
        db.removeLocallyDeletedCheckListEntity(id)
    }

    override suspend fun getAllLocallyDeletedIds(): List<String> {
        return db.getAllLocallyDeletedCheckListIds().asFlow().mapToList().first()
    }
}