package io.silv.jikannoto.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CheckListViewModel : ViewModel() {

    var checkListItems by mutableStateOf((0..10).toList())
}