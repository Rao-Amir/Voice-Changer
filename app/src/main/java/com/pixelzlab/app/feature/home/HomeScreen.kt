package com.pixelzlab.app.feature.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixelzlab.app.R
import com.pixelzlab.app.core.navigation.AppDestination
import com.pixelzlab.app.core.ui.state.UiState
import com.pixelzlab.app.designsystem.component.AppOverlayLoadingWheel
import com.pixelzlab.app.model.entity.Pokemon
import com.pixelzlab.app.utils.extension.collectAsEffect

/**
 * Route composable for Home Screen using UiState pattern
 */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: (destination: AppDestination) -> Unit
) {
    val context = LocalContext.current
    val pokemonState by viewModel.pokemonUiState.collectAsStateWithLifecycle()
    
    // Observe navigation events
    viewModel.navigator.collectAsEffect {
        navigator(it)
    }
    
    // Show errors as toast
    if (pokemonState is UiState.Error) {
        val error = (pokemonState as UiState.Error).error
        Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
    }

    HomeScreen(
        pokemonState = pokemonState,
        onPokemonClick = viewModel::onPokemonClick
    )
}

/**
 * UI composable for Home Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    pokemonState: UiState<List<Pokemon>>,
    onPokemonClick: (Pokemon) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (pokemonState) {
                is UiState.Loading -> {
                    AppOverlayLoadingWheel(
                        modifier = Modifier.align(Alignment.Center),
                        contentDesc = "Loading Pokemons"
                    )
                }
                
                is UiState.Error -> {
                    // Error UI already shown via Toast in HomeRoute
                    // Could add a retry button or more detailed error UI here
                }
                
                is UiState.Success -> {
                    val pokemons = pokemonState.data
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.background(Color.Black)
                    ) {
                        items(pokemons.size) { index ->
                            ItemPokemon(
                                pokemon = pokemons[index],
                                onItemClick = onPokemonClick
                            )
                        }
                    }
                }
            }
        }
    }
}
