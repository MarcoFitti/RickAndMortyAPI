package com.example.rickmortyapi.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.models.domain.Episode
import com.example.rickmortyapi.components.common.LoadingState
import com.example.rickmortyapi.components.common.SimpleToolbar
import com.example.rickmortyapi.components.episode.EpisodeRowComponent
import com.example.rickmortyapi.ui.theme.RickAction
import com.example.rickmortyapi.ui.theme.RickPrimary
import com.example.rickmortyapi.viewmodels.AllEpisodesViewModel

@OptIn(ExperimentalFoundationApi::class)
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

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.data.forEach { mapEntry ->
                        val uniqueCharacterCount = mapEntry.value.flatMap { ep ->
                            ep.characterIdsInEpisode
                        }.toSet().size
                        stickyHeader(key = mapEntry.key) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = RickPrimary)
                            ) {
                                Text(
                                    text = mapEntry.key,
                                    color = Color.Red,
                                    fontSize = 32.sp
                                )
                                Text(
                                    text = "$uniqueCharacterCount unique characters",
                                    color = Color.White,
                                    fontSize = 22.sp
                                )
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .height(4.dp)
                                        .background(
                                            color = RickAction,
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                )

                            }
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
