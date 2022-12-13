package io.silv.jikannoto.domain.mappers

import io.silv.jikannoto.data.models.NetworkNoto
import io.silv.jikannoto.domain.models.NotoItem
import jikannoto.notodb.NotoEntity
import kotlinx.datetime.*

fun NetworkNoto.toDomainNoto(): NotoItem {
    return NotoItem(
        id = id,
        dateCreated = Clock.System.now().toLocalDateTime(TimeZone.UTC),
        title = title,
        content = content,
        synced = true,
        category = category.toCategoryList(),
    )
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

fun NotoEntity.toDomain(): NotoItem {
    return NotoItem(
        id = id,
        dateCreated = Instant.fromEpochMilliseconds(dateCreated).toLocalDateTime(TimeZone.UTC),
        title = title,
        content = content,
        category = category.toCategoryList(),
        synced = synced ?: false,
    )
}

fun String.toCategoryList(): List<String> =
    this.split(",")
        .filter { it.isNotBlank() }
        .ifEmpty { listOf("all") }
