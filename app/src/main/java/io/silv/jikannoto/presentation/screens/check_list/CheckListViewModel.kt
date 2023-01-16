package io.silv.jikannoto.presentation.screens.check_list

import androidx.lifecycle.ViewModel
import io.silv.jikannoto.data.CheckListRepositoryImpl
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.models.dateTimeNow
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container

class CheckListViewModel(
    private val repo: CheckListRepositoryImpl
) : ViewModel(), ContainerHost<CheckListScreenState, CheckListScreenEffect> {

    override val container = container<CheckListScreenState, CheckListScreenEffect>(CheckListScreenState()) {
        init()
    }

    private fun init() = intent {
        repo.getAllItems(false).collect {
            reduce { state.copy(checkListItems = it) }
        }
        repeatOnSubscription {
            repo.checkListFlow.collect {
                reduce { state.copy(checkListItems = it) }
            }
        }
    }

    fun addCheckListItem(name: String) = intent {
        repo.insertItem(checkListItem = CheckListItem(name = name, dateCreated = dateTimeNow(), completed = false))
    }

    fun deleteCheckListItem(id: String) = intent {
        repo.deleteItem(id)
    }
}

data class CheckListScreenState(
    val checkListItems: List<CheckListItem> = emptyList(),
)

sealed class CheckListScreenEffect {
    data class ShowToast(val text: String) : CheckListScreenEffect()
}