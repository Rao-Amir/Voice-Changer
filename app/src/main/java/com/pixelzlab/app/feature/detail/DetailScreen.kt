package com.pixelzlab.app.feature.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.pixelzlab.app.model.entity.Pokemon

/**
 * Route composable for the detail screen
 */
@Composable
fun DetailRoute(
    viewModel: DetailViewModel = hiltViewModel(),
    pokemon: Pokemon? = null,
    onBackClick: () -> Unit = {}
) {
    // We can get Pokemon either from the ViewModel (loaded from navigation args)
    // or directly from the parameter (legacy approach)
    val pokemonFromViewModel by viewModel.pokemon.collectAsStateWithLifecycle()
    val displayPokemon = pokemon ?: pokemonFromViewModel
    
    DetailScreen(
        pokemon = displayPokemon,
        onBackClick = onBackClick
    )
}

/**
 * UI composable for detail screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    pokemon: Pokemon?,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = pokemon?.name ?: "Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = pokemon?.getImageUrl(),
                    contentDescription = pokemon?.name ?: "Pokemon image",
                )
            }
        }
    }
}