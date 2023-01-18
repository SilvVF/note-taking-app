package io.silv.jikannoto.data

import io.silv.jikannoto.data.local.CheckListLocalDataSource
import io.silv.jikannoto.data.remote.CheckListRemoteDataSource
import io.silv.jikannoto.data.util.Crypto
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.domain.mappers.decryptToDomain
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.result.onException
import io.silv.jikannoto.domain.result.onSuccess
import jikannoto.notodb.CheckListEntity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class CheckListRepositoryImpl(
    private val remote: CheckListRemoteDataSource,
    private val local: CheckListLocalDataSource,
    private val appDataStoreRepository: AppDataStoreRepository,
    private val dispatchers: NotoDispatchers,
    private val crypto: Crypto
) {
    private suspend fun getEncryptionKey() = appDataStoreRepository.encryptKeyFlow.first()
        ?: crypto.generateKey(crypto.aesGCMKeySize).also {
                k -> appDataStoreRepository.setEncryptKey(k)
        }

    val checkListFlow = local.getAllItems().mapNotNull { list ->
        list.decryptNotNull(crypto, getEncryptionKey())
    }.flowOn(dispatchers.io)

    suspend fun getAllItems(localOnly: Boolean) = flow {
        val key = getEncryptionKey()
        if (localOnly || !appDataStoreRepository.collectAllFlow.first().sync) {
            emit(local.getAllItems().first().decryptNotNull(crypto, key))
            return@flow
        }
        val localItems = local.getAllItems().first()
        emit(localItems.decryptNotNull(crypto, key))
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
        emit(local.getAllItems().first().decryptNotNull(crypto, key))
        coroutineScope { syncWithNetwork() }
    }.flowOn(dispatchers.io)

    private suspend fun syncWithNetwork() {
        local.getAllLocallyDeletedIds().forEach { id ->
            remote.deleteItemById(id = id).onSuccess {
                local.removeLocallyDeleted(id)
            }
        }
    }

    @OptIn(InternalSerializationApi::class)
    suspend fun insertItem(checkListItem: CheckListItem) {
        kotlin.runCatching {
            val eCheckListItem = CheckListItem(
                name = Json.encodeToString(
                    Crypto.Ciphertext::class.serializer(),
                    crypto.encryptGcm(
                        checkListItem.name.toByteArray(),
                        getEncryptionKey()
                    )
                ),
                id = checkListItem.id,
                dateCreated = checkListItem.dateCreated,
                completed = checkListItem.completed
            )

            coroutineScope {
                local.upsertItem(eCheckListItem)
            }
            coroutineScope {
                remote.upsertItem(eCheckListItem)
            }
        }
    }

    suspend fun deleteItem(id: String) {
        coroutineScope { local.deleteItemById(id) }
        remote.deleteItemById(id).onException {
            local.addLocallyDeleted(id)
        }
    }
}
fun List<CheckListEntity>.decryptNotNull(
    crypto: Crypto,
    key: ByteArray
) = this.mapNotNull {
    it.decryptToDomain(
        crypto,
       key
    )
}