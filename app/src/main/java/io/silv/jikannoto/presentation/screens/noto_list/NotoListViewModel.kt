package io.silv.jikannoto.presentation.screens.noto_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.silv.jikannoto.data.NotoRepositoryImpl
import io.silv.jikannoto.domain.models.NotoItem
import io.silv.jikannoto.presentation.navigation.Screens
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container

class NotoListViewModel(
    private val notoRepo: NotoRepositoryImpl
) : ContainerHost<NotoListState, NotoListSideEffect>, ViewModel() {

    override val container = container<NotoListState, NotoListSideEffect>(NotoListState()) {
        fetchNotos()
        init()
    }

    private var searchJob: Job? = null
    private val _notos = notoRepo.localNotoFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private fun init() = intent {
        repeatOnSubscription {
            _notos.collect {
                launch {
                    if (state.searchText.isBlank()) {
                        reduce {
                            state.copy(
                                notos = it
                            )
                        }
                    }
                }
                launch {
                    val seen = mutableSetOf<String>()
                    val catList = buildList {
                        it.onEach { notoItem ->
                            notoItem.category.forEach { cat ->
                                if (seen.add(cat)) {
                                    add(cat)
                                }
                            }
                        }
                    }
                    reduce { state.copy(categoryList = catList) }
                }
            }
        }
    }

    fun deleteNoto(id: String) = intent {
        notoRepo.deleteNoto(id)
    }

    fun search(text: String) = intent {
        reduce { state.copy(searchText = text) }
        searchJob?.cancel()
        if (text.isNotBlank()) {
            searchJob = viewModelScope.launch {
                reduce {
                    state.copy(
                        notos = _notos.value.filter {
                            text.lowercase() in it.title.lowercase() ||
                                text.lowercase() in it.content.lowercase() ||
                                it.category.any { c -> text.lowercase() in c.lowercase() }
                        }
                    )
                }
            }
        } else reduce { state.copy(notos = _notos.value) }
    }

    fun filterByCategoryChanged(category: String) = intent {
        when (category) {
            in state.categoryFilter -> {
                reduce {
                    state.copy(
                        categoryFilter = state.categoryFilter.filter {
                            it != category
                        }
                            .toSet()
                            .takeIf { it.isNotEmpty() } ?: setOf("all")
                    )
                }
                reduce {
                    state.copy(notos = filterNotos(state.categoryFilter))
                }
            }
            "all" -> reduce {
                state.copy(notos = _notos.value, categoryFilter = setOf("all"))
            }
            else -> {
                reduce {
                    val filter = state.categoryFilter.filter {
                        it != "all"
                    }.toSet() + category
                    state.copy(
                        categoryFilter = filter,
                        notos = filterNotos(filter)
                    )
                }
            }
        }
    }

    private fun filterNotos(categoryList: Set<String>): List<NotoItem> {
        if (categoryList.contains("all")) {
            return _notos.value
        }
        val filteredNotos = _notos.value.filter { noto ->
            categoryList.any { filter ->
                filter in noto.category
            }
        }
        return filteredNotos
    }
    private fun fetchNotos() = intent {
        reduce { state.copy(loading = true) }
        notoRepo.refreshAllNotos(false).collect {
            reduce {
                state.copy(
                    loading = false,
                    notos = it
                )
            }
        }
    }
}

data class NotoListState(
    val loading: Boolean = false,
    val notos: List<NotoItem> = emptyList(),
    val searchText: String = "",
    val categoryFilter: Set<String> = setOf("all"),
    val categoryList: List<String> = emptyList()
)

sealed class NotoListSideEffect {
    data class Navigate(val route: Screens) : NotoListSideEffect()
    object NotoSaved : NotoListSideEffect()
}
