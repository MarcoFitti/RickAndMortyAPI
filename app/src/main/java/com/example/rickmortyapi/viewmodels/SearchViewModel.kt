package com.example.rickmortyapi.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.models.domain.Character
import com.example.rickmortyapi.NavDestination
import com.example.rickmortyapi.repositories.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {
    val searchTextFieldState = TextFieldState()

    sealed interface SearchState {
        object Empty : SearchState
        data class UserQuery(val query : String) : SearchState
    }

    sealed interface ScreenState {
        object  Empty : ScreenState
        object Searching : ScreenState
        data class Error(val message : String) : ScreenState
        data class Content(
            val userQuery : String,
            val results : List<Character>
        ) : SearchState
    }

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val searchTextState = snapshotFlow { searchTextFieldState.text }
        .debounce(500)
        .mapLatest { text ->
            if (text.isBlank())  SearchState.Empty
            else SearchState.UserQuery(text.toString())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 2000),
            initialValue = ""
        )

    fun observeUserSearh() = viewModelScope.launch {
        searchTextState.collectLatest { searchState ->
            when (searchState) {
                is SearchState.Empty -> _uiState.update { ScreenState.Empty }
                is SearchState.UserQuery -> {

                }
            }
        }

    }

    private fun searchAllCharacter(query : String) = viewModelScope.launch {
        _uiState.update { ScreenState.Searching }
        characterRepository.fetchAllCharacterByName(searchQuery = query)
            .onSuccess { characters ->
                ScreenState.Content(
                    userQuery = query,
                    results = characters
                )

            }
            .onFailure {
                _uiState.update { ScreenState.Error(exception.message ?: "Error") }

            }

    }
}