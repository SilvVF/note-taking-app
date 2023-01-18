package io.silv.jikannoto.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import io.silv.jikannoto.data.local.NotoLocalDataSource
import io.silv.jikannoto.data.models.NetworkNoto
import io.silv.jikannoto.data.remote.NotoRemoteDataSource
import io.silv.jikannoto.data.util.Crypto
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.domain.mappers.decrpytToDomain
import io.silv.jikannoto.domain.mappers.toEntity
import io.silv.jikannoto.domain.models.NotoItem
import io.silv.jikannoto.domain.result.NotoApiResult
import io.silv.jikannoto.domain.result.NotoFetchResult
import io.silv.jikannoto.domain.result.onException
import io.silv.jikannoto.domain.result.onSuccess
import java.util.UUID
import jikannoto.notodb.NotoEntity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class NotoRepositoryImpl(
    private val localDataSource: NotoLocalDataSource,
    private val remoteDataSource: NotoRemoteDataSource,
    private val firebaseAuth: FirebaseAuth,
    private val appDataStoreRepository: AppDataStoreRepository,
    private val dispatcher: NotoDispatchers,
    private val crypto: Crypto
) {
    private suspend fun getEncryptionKey() = appDataStoreRepository.encryptKeyFlow.first()
            ?: crypto.generateKey(crypto.aesGCMKeySize).also {
                    k -> appDataStoreRepository.setEncryptKey(k)
            }



    val localNotoFlow = localDataSource.getAllNotos().map {
        it.mapNotNull { entity ->
            entity.decrpytToDomain(crypto, getEncryptionKey())
        }
    }.flowOn(dispatcher.io)

    suspend fun refreshAllNotos(
        localOnly: Boolean
    ): Flow<List<NotoItem>> = flow {
        if (!localOnly) {
            firebaseAuth.ifValidEmail(default = NotoFetchResult.Empty()) { email ->
                remoteDataSource.fetchAllNotos(email)
            }.onSuccess { data ->
                if (appDataStoreRepository.collectAllFlow.first().sync) {
                    syncNotosFromNetwork(data)
                }
            }
        }
        emit(
            localDataSource.getAllNotos().first().mapNotNull {
                it.decrpytToDomain(
                    crypto,
                    getEncryptionKey()
                )
            }
        )
    }.flowOn(dispatcher.io)

    private suspend fun syncNotosFromNetwork(notos: List<NetworkNoto>) {
        notos.forEach { networkNoto ->
            localDataSource.insertNoto(
                networkNoto.toEntity()
            )
        }
        coroutineScope {
            localDataSource.getAllLocallyDeletedIds().forEach {
                remoteDataSource.deleteNoto(it)
                    .onSuccess {
                        localDataSource.handleLocallyDeleted(it)
                    }
            }
        }
    }

    @OptIn(InternalSerializationApi::class)
    suspend fun upsertNoto(
        id: String = UUID.randomUUID().toString(),
        title: String,
        content: String,
        category: String,
        dateCreated: Long = Clock.System.now().toEpochMilliseconds()
    ) = withContext(dispatcher.io) {

        var result: NotoApiResult<Nothing>? = null
        var userEmail: String? = null
        val s = Crypto.Ciphertext::class.serializer()

        runCatching {

            val key = getEncryptionKey()

            val etitle = Json.encodeToString(s, crypto.encryptGcm(title.toByteArray(), key))
            val econtent = Json.encodeToString(s, crypto.encryptGcm(content.toByteArray(), key))
            val ecategory = Json.encodeToString(s, crypto.encryptGcm(category.toByteArray(), key))

            if (appDataStoreRepository.collectAllFlow.first().sync) {
                firebaseAuth.ifValidEmail(NotoApiResult.Exception<Nothing>(null) to null) { email ->
                    result = remoteDataSource.upsertNoto(
                        NetworkNoto(id, dateCreated, etitle, econtent, email, ecategory)
                    )
                    userEmail = email
                }
            }
            localDataSource.insertNoto(
                NotoEntity(
                    id, etitle, econtent, userEmail ?: "", ecategory,
                    synced = result is NotoApiResult.Success<*>,
                    dateCreated = dateCreated,
                    lastSync = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    suspend fun deleteNoto(id: String) = withContext(dispatcher.io) {
        Log.d("notos", "repo deleteNoto invoked with id:$id")
        localDataSource.deleteNotoById(id)
        if (appDataStoreRepository.collectAllFlow.first().sync) {
            remoteDataSource.deleteNoto(id)
                .onException {
                    localDataSource.addLocallyDeleted(id)
                }
        } else {
            localDataSource.addLocallyDeleted(id)
        }
    }
}

suspend fun <T> FirebaseAuth.ifValidEmail(default: T, executable: suspend (String) -> T): T {
    val email = this.currentUser?.email
    return email?.let {
        executable(email)
    } ?: default
}
