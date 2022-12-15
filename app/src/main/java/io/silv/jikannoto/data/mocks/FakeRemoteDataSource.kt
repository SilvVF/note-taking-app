package io.silv.jikannoto.data.mocks

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import io.silv.jikannoto.data.models.NetworkNoto
import io.silv.jikannoto.data.remote.NotoRemoteDataSource
import io.silv.jikannoto.domain.result.NotoApiResult
import io.silv.jikannoto.domain.result.NotoFetchResult
import kotlinx.datetime.Clock
import java.util.*
import kotlin.random.Random

class FakeRemoteDataSource: NotoRemoteDataSource {
    override suspend fun deleteNoto(id: String): NotoApiResult<Nothing> {
        return listOf(NotoApiResult.Success<Nothing>(), NotoApiResult.Exception("error")).random()
    }

    override suspend fun upsertNoto(noto: NetworkNoto): NotoApiResult<Nothing> {
        return listOf(NotoApiResult.Success<Nothing>(), NotoApiResult.Exception("error")).random()
    }

    override suspend fun fetchAllNotos(owner: String): NotoFetchResult<List<NetworkNoto>> {
       return listOf(
           NotoFetchResult.Success((0..100).map {
               NetworkNoto(
                    id = UUID.randomUUID().toString(),
                    title = LoremIpsum(
                        Random(Clock.System.now().toEpochMilliseconds()).nextInt(from = 5, until = 12)
                    ).values.joinToString(),
                    content = LoremIpsum(
                        Random(Clock.System.now().toEpochMilliseconds()).nextInt(from = 17, until = 50)
                    ).values.joinToString(),
                    owner = "user@gmail.com",
                    dateCreated = Clock.System.now().toEpochMilliseconds(),
                    category = "all",
                )
           }),
           NotoFetchResult.Empty()
       ).random()
    }
}