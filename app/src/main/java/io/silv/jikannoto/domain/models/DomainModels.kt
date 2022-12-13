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
    val id: String,
    val dateCreated: LocalDateTime,
    val title: String,
    val synced: Boolean,
    val completed: Boolean,
    val priority: Priority
)

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val profilePictureUrl: String? = null
)

sealed class Resource <T> {
    class Loading <T> : Resource<T>()

    data class Done <T> (val data: T?) : Resource<T>()
}

sealed class Priority(val value: Int) {

    object High : Priority(1)
    object Med : Priority(2)
    object Low : Priority(3)

    companion object {
        fun fromNumber(i: Number): Priority {
            return when (i) {
                1 -> High
                2 -> Med
                3 -> Low
                else -> Med
            }
        }
    }
}
