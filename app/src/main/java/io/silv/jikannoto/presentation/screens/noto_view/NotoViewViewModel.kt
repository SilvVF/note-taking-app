@file:OptIn(OrbitExperimental::class)

package io.silv.jikannoto.presentation.screens.noto_view

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import io.silv.jikannoto.data.NotoRepositoryImpl
import io.silv.jikannoto.domain.models.NotoItem
import java.time.ZoneOffset
import javax.annotation.concurrent.Immutable
import kotlinx.datetime.toJavaLocalDateTime
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.*
import org.orbitmvi.orbit.viewmodel.container

class NotoViewViewModel(
    private val notoRepo: NotoRepositoryImpl
) : ViewModel(), ContainerHost<NotoViewState, NotoViewSideEffect> {

    override val container = container<NotoViewState, NotoViewSideEffect>(NotoViewState()) {
        init()
    }

    private var notoReceivedState: NotoItem? = null
    private fun init() = intent {
        repeatOnSubscription {
            notoRepo.localNotoFlow.collect {
                val uniqueCategory = buildSet {
                    it.onEach { notoItem ->
                        notoItem.category.forEach { cat ->
                            add(cat.trim())
                        }
                    }
                }
                reduce { state.copy(categoryList = uniqueCategory.toList()) }
            }
        }
    }

    fun initialNotoItem(noto: NotoItem?) = intent {
        noto?.let {
            reduce {
                state.copy(noto = it)
            }
            notoReceivedState = it
        }
    }

    fun addNoto(noto: NotoItem) = intent {
        if (noto != notoReceivedState)
            notoRepo.upsertNoto(
                id = noto.id,
                title = noto.title,
                content = noto.content,
                category = noto.category.toString().removeSurrounding(prefix = "[", suffix = "]").trim(),
                dateCreated = noto.dateCreated.toJavaLocalDateTime().toEpochSecond(ZoneOffset.UTC) * 1000
            )
        postSideEffect(NotoViewSideEffect.NotoSaved)
    }

    fun handleTitleChange(title: String) = blockingIntent {
        reduce {
            state.copy(
                noto = state.noto.copy(title = title)
            )
        }
    }

    fun handleContentChange(content: String) = blockingIntent {
        reduce {
            state.copy(
                noto = state.noto.copy(content = content)
            )
        }
    }

    fun handleCategoryChange(category: String, selected: Boolean) = intent {
        val trimmedCategory = category.trim()
        reduce {
            state.copy(
                noto = state.noto.copy(
                    // Select and unselect by name if not wanted move && ...
                    category = if (selected && trimmedCategory !in state.noto.category) {
                        state.noto.category + trimmedCategory
                    } else {
                        state.noto.category.filter { it != trimmedCategory }
                    }
                )
            )
        }
        if (trimmedCategory !in state.categoryList) {
            reduce {
                state.copy(
                    categoryList = state.categoryList + trimmedCategory
                )
            }
        }
    }
}

@Immutable
@Stable
data class NotoViewState(
    val noto: NotoItem = NotoItem.blankItem(),
    val categoryList: List<String> = emptyList(),
)

sealed class NotoViewSideEffect {
    object NotoSaved : NotoViewSideEffect()
}