package io.silv.jikannoto.presentation.screens.noto_view

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import io.silv.jikannoto.data.NotoRepositoryImpl
import io.silv.jikannoto.domain.models.NotoItem
import java.time.ZoneOffset
import javax.annotation.concurrent.Immutable
import kotlinx.datetime.toJavaLocalDateTime
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container

class NotoViewViewModel(
    private val notoRepo: NotoRepositoryImpl
) : ViewModel(), ContainerHost<NotoViewState, NotoViewSideEffect> {

    override val container = container<NotoViewState, NotoViewSideEffect>(NotoViewState()) {
        init()
    }
    private fun init() = intent {
        repeatOnSubscription {
            notoRepo.localNotoFlow.collect {
                val uniqueCategorys = buildSet {
                    it.onEach { notoItem ->
                        notoItem.category.forEach { cat ->
                            add(cat.trim())
                        }
                    }
                }
                reduce { state.copy(categoryList = uniqueCategorys.toList()) }
            }
        }
    }

    fun initialNotoItem(noto: NotoItem?) = intent {
        noto?.let {
            reduce {
                state.copy(noto = it)
            }
        }
    }

    fun addNoto(noto: NotoItem) = intent {
        notoRepo.upsertNoto(
            id = noto.id,
            title = noto.title,
            content = noto.content,
            category = noto.category.toString().removeSurrounding(prefix = "[", suffix = "]"),
            dateCreated = noto.dateCreated.toJavaLocalDateTime().toEpochSecond(ZoneOffset.UTC) * 1000
        )
        postSideEffect(NotoViewSideEffect.NotoSaved)
    }
    fun handleTitleChange(title: String) = intent {
        reduce {
            state.copy(
                noto = state.noto.copy(title = title)
            )
        }
    }

    fun handleContentChange(content: String) = intent {
        reduce {
            state.copy(
                noto = state.noto.copy(content = content)
            )
        }
    }

    fun handleCategoryChange(category: String, selected: Boolean) = intent {
        reduce {
            state.copy(
                noto = state.noto.copy(
                    category = if (selected) {
                        state.noto.category + category
                    } else {
                        state.noto.category.filter { it != category }
                    }
                )
            )
        }
        if (category !in state.categoryList) {
            reduce {
                state.copy(
                    categoryList = state.categoryList + category
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