package io.silv.jikannoto.data.local

import android.util.Log
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import io.silv.jikannoto.NotoDatabase
import io.silv.jikannoto.data.util.NotoDispatchers
import jikannoto.notodb.NotoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class NotoLocalDataSourceImpl(
    db: NotoDatabase,
    private val dispatcher: NotoDispatchers
) : NotoLocalDataSource {

    private val queries = db.notoEntityQueries

    override suspend fun getNotoById(
        id: String
    ): NotoEntity? = withContext(dispatcher.io) {
        queries.getNotoById(id).executeAsOneOrNull()
    }

    override fun getAllNotos(): Flow<List<NotoEntity>> =
        queries.getAllNotos().asFlow().mapToList().flowOn(dispatcher.io)

    override suspend fun insertNoto(notoEntity: NotoEntity) {
        queries.upsertNoto(
            notoEntity.id,
            notoEntity.title,
            notoEntity.content,
            notoEntity.owner,
            notoEntity.category,
            notoEntity.synced,
            notoEntity.dateCreated,
            notoEntity.lastSync
        )
    }

    override suspend fun deleteNotoById(
        id: String
    ) = withContext(dispatcher.io) {
        Log.d("notos", "local datasource deleteNoto invoked with id:$id")
        queries.deleteNotoById(id)
    }

    override suspend fun deleteAllNotos() =
        withContext(dispatcher.io) { queries.clearAllNotoEntitys() }

    override suspend fun addLocallyDeleted(id: String) =
        withContext(dispatcher.io) {
            queries.addLocallyDeleted(id)
        }

    override suspend fun getAllLocallyDeletedIds(): List<String> =
        queries.getAllLocallyDeleted().asFlow().mapToList().first()

    override suspend fun handleLocallyDeleted(id: String) =
        withContext(dispatcher.io) {
            queries.locallyDeletedHandled(id)
        }
}