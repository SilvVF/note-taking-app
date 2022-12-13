package io.silv.jikannoto.data.local

import jikannoto.notodb.NotoEntity
import kotlinx.coroutines.flow.Flow

interface NotoLocalDataSource {

    suspend fun getNotoById(id: String): NotoEntity?

    /**
     * will be triggered on update listen to flow to get live
     * updates on insertions and deletions
     */
    fun getAllNotos(): Flow<List<NotoEntity>>

    suspend fun insertNoto(notoEntity: NotoEntity)

    suspend fun deleteNotoById(id: String)

    suspend fun deleteAllNotos()

    suspend fun addLocallyDeleted(id: String)

    suspend fun getAllLocallyDeletedIds(): List<String>

    suspend fun handleLocallyDeleted(id: String)
}