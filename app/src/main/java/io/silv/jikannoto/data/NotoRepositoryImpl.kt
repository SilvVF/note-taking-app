package io.silv.jikannoto.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import io.silv.jikannoto.data.local.NotoLocalDataSource
import io.silv.jikannoto.data.models.NetworkNoto
import io.silv.jikannoto.data.remote.NotoRemoteDataSource
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.domain.mappers.toDomain
import io.silv.jikannoto.domain.mappers.toEntity
import io.silv.jikannoto.domain.models.NotoItem
import io.silv.jikannoto.domain.result.NotoApiResult
import io.silv.jikannoto.domain.result.NotoFetchResult
import io.silv.jikannoto.domain.result.onException
import io.silv.jikannoto.domain.result.onSuccess
import java.util.UUID
import jikannoto.notodb.NotoEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class NotoRepositoryImpl(
    private val localDataSource: NotoLocalDataSource,
    private val remoteDataSource: NotoRemoteDataSource,
    private val firebaseAuth: FirebaseAuth,
    private val dispatcher: NotoDispatchers
) {
    val localNotoFlow = localDataSource.getAllNotos().map {
        it.map { entity ->
            entity.toDomain()
        }
    }.flowOn(dispatcher.io)

    suspend fun refreshAllNotos(
        localOnly: Boolean
    ): Flow<List<NotoItem>> = flow {
        if (!localOnly) {
            firebaseAuth.ifValidEmail(default = NotoFetchResult.Empty()) { email ->
                remoteDataSource.fetchAllNotos(email)
            }.onSuccess { data ->
                syncNotosFromNetwork(data)
            }
        }
        emit(localDataSource.getAllNotos().first().map { it.toDomain() })
    }.flowOn(dispatcher.io)

    private suspend fun syncNotosFromNetwork(notos: List<NetworkNoto>) {
        notos.forEach { networkNoto ->
            localDataSource.insertNoto(
                networkNoto.toEntity()
            )
        }
    }

    suspend fun upsertNoto(
        id: String = UUID.randomUUID().toString(),
        title: String,
        content: String,
        category: String,
        dateCreated: Long = Clock.System.now().toEpochMilliseconds()
    ) = withContext(dispatcher.io) {
        val (result, email) = firebaseAuth.ifValidEmail(NotoApiResult.Exception<Nothing>(null) to null) { email ->
            remoteDataSource.upsertNoto(
                NetworkNoto(id, dateCreated, title, content, email, category)
            ) to email
        }
        localDataSource.insertNoto(
            NotoEntity(
                id, title, content, email ?: "", category,
                synced = result is NotoApiResult.Success,
                dateCreated = dateCreated,
                lastSync = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    suspend fun deleteNoto(id: String) = withContext(dispatcher.io) {
        Log.d("notos", "repo deleteNoto invoked with id:$id")
        localDataSource.deleteNotoById(id)
        remoteDataSource.deleteNoto(id)
            .onException {
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
