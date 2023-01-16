package io.silv.jikannoto.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import io.silv.jikannoto.data.Collections
import io.silv.jikannoto.data.models.NetworkNoto
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.data.util.defaultEventHandles
import io.silv.jikannoto.domain.result.NotoApiResult
import io.silv.jikannoto.domain.result.NotoFetchResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NotoRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val dispatchers: NotoDispatchers
) : NotoRemoteDataSource {
    override suspend fun deleteNoto(
        id: String
    ) = withContext<NotoApiResult<Nothing>>(dispatchers.io) {
        Log.d("notos", "remote datasource deleteNoto invoked with id:$id")
        try {
            val data = firestore.collection(Collections.noto)
                .whereEqualTo("id", id)
                .get()
                .await()
            data.documents.forEach { documentSnapshot ->
                documentSnapshot.reference.delete()
            }
            NotoApiResult.Success()
        } catch (e: Exception) {
            NotoApiResult.Exception(e.message)
        }
    }

    override suspend fun upsertNoto(
        noto: NetworkNoto
    ) = suspendCancellableCoroutine<NotoApiResult<Nothing>> { continuation ->
        firestore.collection(Collections.noto)
            .add(noto)
            .defaultEventHandles(
                continuation = continuation
            )
    }

    override suspend fun fetchAllNotos(owner: String) =
        withContext(dispatchers.io) {
            try {
                val result = firestore.collection(Collections.noto)
                    .whereEqualTo("owner", owner)
                    .get()
                    .await()
                val notoList = mutableListOf<NetworkNoto>()
                result.documents.forEach { document ->
                    document.toObject<NetworkNoto>()?.let {
                        noto ->
                        notoList.add(noto)
                    }
                }
                NotoFetchResult.Success(notoList.toList())
            } catch (e: Exception) {
                NotoFetchResult.Empty()
            }
        }
}