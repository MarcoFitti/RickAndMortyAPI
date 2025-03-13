package com.example.rickmortyapi.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.models.domain.Episode
import com.example.rickmortyapi.components.common.LoadingState
import com.example.rickmortyapi.components.common.SimpleToolbar
import com.example.rickmortyapi.components.episode.EpisodeRowComponent
import com.example.rickmortyapi.viewmodels.AllEpisodesViewModel

@Composable
fun AllEpisodesScreen(
    episodesViewModel : AllEpisodesViewModel = hiltViewModel()
) {
     val uiState by episodesViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        episodesViewModel.refreshAllEpisodes()
    }

    when (val state = uiState) {
        AllEpisodesUiState.Error -> {
            //TODO

        }
        AllEpisodesUiState.Loading -> LoadingState()
        is AllEpisodesUiState.Success -> {
            Column {
                SimpleToolbar(title = "All episodes")

                LazyColumn {
                    state.data.forEach { mapEntry ->
                        item(key = mapEntry.key) {
                            Text(
                                text = mapEntry.key,
                                color = Color.Red,
                                fontSize = 32.sp
                            )
                        }
                        mapEntry.value.forEach { episode ->
                                item(key = episode.id) { EpisodeRowComponent(episode = episode) }
                        }


                    }
                }
            }
        }

    }


}

sealed interface AllEpisodesUiState {
    object Error : AllEpisodesUiState
    object Loading : AllEpisodesUiState
    data class Success(val data : Map<String, List<Episode>>) : AllEpisodesUiState
}
