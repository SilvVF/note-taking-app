package io.silv.jikannoto.data.models

import java.util.*

data class NetworkNoto(
    val id: String = UUID.randomUUID().toString(),
    val dateCreated: Long = 0,
    val title: String = "",
    val content: String = "",
    val owner: String = "",
    val category: String = "all"
)

data class NetworkChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val dateCreated: Long,
    val title: String,
    val synced: Boolean,
    val completed: Boolean,
    val priority: Int
)

data class NetworkUser(
    val email: String,
    val password: String,
    val id: String = UUID.randomUUID().toString()
)