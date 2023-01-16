package io.silv.jikannoto.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import io.silv.jikannoto.data.Collections
import io.silv.jikannoto.data.models.NetworkChecklistItem
import io.silv.jikannoto.data.util.NotoDispatchers
import io.silv.jikannoto.data.util.defaultEventHandles
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.result.NotoApiResult
import io.silv.jikannoto.domain.result.NotoFetchResult
import java.time.ZoneOffset
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.datetime.toJavaLocalDateTime

class CheckListRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val dispatcher: NotoDispatchers
) : CheckListRemoteDataSource {
    override suspend fun getAllItems() = flow {
        val data = runCatching {
            firestore.collection(Collections.checklist)
                .whereEqualTo("owner", auth.currentUser?.email)
                .get()
                .await()
        }.getOrNull() ?: run {
            emit(NotoFetchResult.Empty())
            return@flow
        }
        val checkListItems = buildList {
            data.documents.forEach { documentSnapshot ->
                documentSnapshot.toObject<NetworkChecklistItem>()?.let {
                    add(it)
                }
            }
        }
        if (checkListItems.isEmpty()) {
            emit(NotoFetchResult.Empty())
        } else {
            emit(NotoFetchResult.Success(checkListItems))
        }
    }

    override suspend fun deleteItemById(id: String) = withContext<NotoApiResult<Nothing>>(dispatcher.io) {
        Log.d("notos", "remote datasource deleteNoto invoked with id:$id")
        try {
            val data = firestore.collection(Collections.checklist)
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

    override suspend fun upsertItem(checklistItem: CheckListItem) = suspendCancellableCoroutine<NotoApiResult<Nothing>> { continuation ->
        firestore.collection(Collections.checklist)
            .add(
                NetworkChecklistItem(
                    id = checklistItem.id,
                    name = checklistItem.name,
                    dateCreated = checklistItem.dateCreated.toJavaLocalDateTime().toEpochSecond(
                        ZoneOffset.UTC
                    ) * 1000,
                    completed = checklistItem.completed
                )
            )
            .defaultEventHandles(
                continuation = continuation
            )
    }
}