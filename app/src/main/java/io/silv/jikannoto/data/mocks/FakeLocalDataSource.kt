package io.silv.jikannoto.data.mocks

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import io.silv.jikannoto.data.local.NotoLocalDataSource
import jikannoto.notodb.NotoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import java.util.UUID
import kotlin.random.Random

class FakeLocalDataSource: NotoLocalDataSource {

    private var notoList = (0..100).map {
        NotoEntity(
            id = UUID.randomUUID().toString(),
            title = LoremIpsum(
                Random(Clock.System.now().toEpochMilliseconds()).nextInt(from = 5, until = 12)
            ).values.joinToString(),
            content = LoremIpsum(
                Random(Clock.System.now().toEpochMilliseconds()).nextInt(from = 17, until = 50)
            ).values.joinToString(),
            owner = "user@gmail.com",
            synced = false,
            dateCreated = Clock.System.now().toEpochMilliseconds(),
            category = "all",
            lastSync = null
        )
    }.toMutableList()

    override suspend fun getNotoById(id: String): NotoEntity? {
        return NotoEntity(
            id = UUID.randomUUID().toString(),
            title = LoremIpsum(
                Random(Clock.System.now().toEpochMilliseconds()).nextInt(from = 5, until = 12)
            ).values.joinToString(),
            content = LoremIpsum(
                Random(Clock.System.now().toEpochMilliseconds()).nextInt(from = 17, until = 50)
            ).values.joinToString(),
            owner = "user@gmail.com",
            synced = false,
            dateCreated = Clock.System.now().toEpochMilliseconds(),
            category = "all",
            lastSync = null
        )
    }

    override fun getAllNotos(): Flow<List<NotoEntity>> {
        return flow {
            emit(notoList)
        }
    }

    override suspend fun insertNoto(notoEntity: NotoEntity) {
        notoList.add(notoEntity)
    }

    override suspend fun deleteNotoById(id: String) {
        notoList = notoList.filter { it.id != id }.toMutableList()
    }

    override suspend fun deleteAllNotos() {
        notoList = mutableListOf()
    }

    override suspend fun addLocallyDeleted(id: String) = Unit
    override suspend fun getAllLocallyDeletedIds(): List<String> = emptyList()
    override suspend fun handleLocallyDeleted(id: String) = Unit
}