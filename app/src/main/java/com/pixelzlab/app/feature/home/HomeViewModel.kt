package com.pixelzlab.app.feature.home

import androidx.lifecycle.viewModelScope
import com.pixelzlab.app.core.base.BaseViewModel
import com.pixelzlab.app.core.navigation.AppDestination
import com.pixelzlab.app.core.ui.state.UiState
import com.pixelzlab.app.data.remote.repository.AppRepository
import com.pixelzlab.app.di.IoDispatcher
import com.pixelzlab.app.model.entity.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Home screen with modern state holder pattern
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepository: AppRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel() {

    /**
     * UI state for Pokémon list
     */
    val pokemonUiState: StateFlow<UiState<List<Pokemon>>> = appRepository.fetchPokemons()
        .map<List<Pokemon>, UiState<List<Pokemon>>> { UiState.Success(it) }
        .catch { e ->
            Timber.e(e, "Error loading Pokémon")
            emit(UiState.Error(e))
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )
    
    /**
     * Handle Pokémon click
     */
    fun onPokemonClick(pokemon: Pokemon) {
        viewModelScope.launch {
            _navigator.emit(AppDestination.Detail.addParcel(pokemon))
        }
    }
}
