package io.silv.jikannoto.presentation.screens.check_list

import androidx.lifecycle.ViewModel
import io.silv.jikannoto.data.CheckListRepositoryImpl
import io.silv.jikannoto.domain.models.CheckListItem
import io.silv.jikannoto.domain.models.dateTimeNow
import java.util.UUID
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
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

    fun addCheckListItem(name: String, id: String = UUID.randomUUID().toString()) = intent {
        if (name.isBlank()) {
            postSideEffect(CheckListScreenEffect.BlankCheckListItemError)
            return@intent
        }
        repo.insertItem(checkListItem = CheckListItem(name = name, dateCreated = dateTimeNow(), completed = false, id = id))
    }

    fun deleteCheckListItem(id: String) = intent {
        repo.deleteItem(id)
    }
}

data class CheckListScreenState(
    val checkListItems: List<CheckListItem> = emptyList(),
)

sealed class CheckListScreenEffect {
    object BlankCheckListItemError : CheckListScreenEffect()
    data class ShowToast(val text: String) : CheckListScreenEffect()
}