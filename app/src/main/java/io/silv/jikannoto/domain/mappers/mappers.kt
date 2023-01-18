package io.silv.jikannoto.domain.mappers

import io.silv.jikannoto.data.models.NetworkNoto
import io.silv.jikannoto.data.util.Crypto
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.models.NotoItem
import jikannoto.notodb.CheckListEntity
import jikannoto.notodb.NotoEntity
import kotlinx.datetime.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// fun NetworkNoto.toDomainNoto(): NotoItem {
//    return NotoItem(
//        id = id,
//        dateCreated = Clock.System.now().toLocalDateTime(TimeZone.UTC),
//        title = title,
//        content = content,
//        synced = true,
//        category = category.toCategoryList(),
//    )
// }

fun NotoEntity.decrpytToDomain(crypto: Crypto, key: ByteArray): NotoItem? {

    return runCatching {

        val titleCipher = Json.decodeFromString<Crypto.Ciphertext>(title)
        val contentCipher = Json.decodeFromString<Crypto.Ciphertext>(content)
        val categoryCipher = Json.decodeFromString<Crypto.Ciphertext>(category)

        NotoItem(
            id = id,
            title = crypto.decryptGcm(titleCipher, key).decodeToString(),
            dateCreated = Instant.fromEpochMilliseconds(dateCreated).toLocalDateTime(TimeZone.currentSystemDefault()),
            content = crypto.decryptGcm(contentCipher, key).decodeToString(),
            category = crypto.decryptGcm(categoryCipher, key).decodeToString().toCategoryList(),
            synced = synced ?: false,
        )
    }.getOrNull()
}

fun CheckListEntity.decryptToDomain(crypto: Crypto, key: ByteArray): CheckListItem? {
    return kotlin.runCatching {
        val nameCipher = Json.decodeFromString<Crypto.Ciphertext>(name)

        CheckListItem(
            id = id,
            dateCreated = Instant.fromEpochMilliseconds(dateCreated).toLocalDateTime(TimeZone.UTC),
            name = crypto.decryptGcm(nameCipher, key).decodeToString(),
            completed = completed ?: false
        )
    }.getOrNull()
}

fun NetworkNoto.toEntity(): NotoEntity {
    return NotoEntity(
        id = id,
        dateCreated = dateCreated,
        title = title,
        content = content,
        owner = owner,
        category = category,
        synced = true,
        lastSync = Clock.System.now().toEpochMilliseconds(),
    )
}

// fun NotoEntity.toDomain(): NotoItem {
//    return NotoItem(
//        id = id,
//        dateCreated = Instant.fromEpochMilliseconds(dateCreated).toLocalDateTime(TimeZone.UTC),
//        title = title,
//        content = content,
//        category = category.toCategoryList(),
//        synced = synced ?: false,
//    )
// }
//
// fun CheckListEntity.toDomain(): CheckListItem {
//    return CheckListItem(
//        id = id,
//        dateCreated = Instant.fromEpochMilliseconds(dateCreated).toLocalDateTime(TimeZone.UTC),
//        name = name,
//        completed = completed ?: false
//    )
// }

fun String.toCategoryList(): List<String> =
    this.split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toSet().toList()
        .ifEmpty { listOf("all") }
