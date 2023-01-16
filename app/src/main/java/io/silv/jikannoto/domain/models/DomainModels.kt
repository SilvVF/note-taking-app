package io.silv.jikannoto.domain.models

import java.util.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun dateTimeNow(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.UTC)
}
data class NotoItem(
    val id: String,
    val category: List<String>,
    val dateCreated: LocalDateTime,
    val title: String,
    val content: String,
    val synced: Boolean,
) {
    companion object {
        fun blankItem(): NotoItem =
            NotoItem(UUID.randomUUID().toString(), emptyList(), dateTimeNow(), "", "", false)
    }
}
data class CheckListItem(
    val id: String = UUID.randomUUID().toString(),
    val dateCreated: LocalDateTime,
    val name: String,
    val completed: Boolean,
)

data class User(
    val email: String = "",
)
