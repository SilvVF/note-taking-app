package io.silv.jikannoto.data

import io.silv.jikannoto.data.local.CheckListLocalDataSource
import io.silv.jikannoto.data.remote.CheckListRemoteDataSource
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.domain.mappers.toDomain
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.result.onException
import io.silv.jikannoto.domain.result.onSuccess
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*

class CheckListRepositoryImpl(
    private val remote: CheckListRemoteDataSource,
    private val local: CheckListLocalDataSource,
    private val appDataStoreRepository: AppDataStoreRepository,
    private val dispatchers: NotoDispatchers
) {

    val checkListFlow = local.getAllItems().map { list ->
        list.map { it.toDomain() }
    }.flowOn(dispatchers.io)

    suspend fun getAllItems(localOnly: Boolean) = flow<List<CheckListItem>> {
        if (localOnly || !appDataStoreRepository.collectAllFlow.first().sync) {
            emit(local.getAllItems().first().map { it.toDomain() })
            return@flow
        }
        val localItems = local.getAllItems().first()
        emit(localItems.map { it.toDomain() })
        remote.getAllItems().first().onSuccess { data ->
            data.forEach { networkItem ->
                local.upsertItem(
                    CheckListItem(
                        networkItem.id,
                        Instant.fromEpochMilliseconds(networkItem.dateCreated)
                            .toLocalDateTime(TimeZone.UTC),
                        networkItem.name,
                        networkItem.completed
                    )
                )
            }
        }
        emit(local.getAllItems().first().map { it.toDomain() })
        coroutineScope { syncWithNetwork() }
    }.flowOn(dispatchers.io)

    private suspend fun syncWithNetwork() {
        local.getAllLocallyDeletedIds().forEach { id ->
            remote.deleteItemById(id = id).onSuccess {
                local.removeLocallyDeleted(id)
            }
        }
    }

    suspend fun insertItem(checkListItem: CheckListItem) {
        coroutineScope {
            local.upsertItem(checkListItem)
        }
        coroutineScope {
            remote.upsertItem(checkListItem)
        }
    }

    suspend fun deleteItem(id: String) {
        coroutineScope { local.deleteItemById(id) }
        remote.deleteItemById(id).onException {
            local.addLocallyDeleted(id)
        }
    }
}