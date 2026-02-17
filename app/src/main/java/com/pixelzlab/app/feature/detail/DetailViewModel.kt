package com.pixelzlab.app.feature.detail

import androidx.lifecycle.SavedStateHandle
import com.pixelzlab.app.core.base.BaseViewModel
import com.pixelzlab.app.feature.detail.navigation.DetailArgs
import com.pixelzlab.app.model.entity.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel for detail screen using type-safe navigation arguments
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val _pokemon = MutableStateFlow<Pokemon?>(null)
    val pokemon: StateFlow<Pokemon?> = _pokemon

    init {
        // Get the pokemon from navigation args
        val detailArgs = DetailArgs.fromSavedStateHandle(savedStateHandle)
        _pokemon.value = detailArgs.pokemon
    }
} 