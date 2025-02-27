package com.example.rickmortyapi.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.KtorClient
import com.example.network.models.domain.Character
import com.example.network.models.domain.Episode
import com.example.rickmortyapi.components.common.CharacterImage
import com.example.rickmortyapi.components.common.CharacterNameComponent
import com.example.rickmortyapi.components.common.LoadingState
import com.example.rickmortyapi.components.episode.EpisodeRowComponent
import com.example.rickmortyapi.ui.theme.RickAction
import com.example.rickmortyapi.ui.theme.RickPrimary
import com.example.rickmortyapi.ui.theme.RickTextPrimary
import kotlinx.coroutines.launch

@Composable
fun CharacterEpisodeScreen(
    characterId: Int, ktorClient: KtorClient
) {/*
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Character episode screen: $characterId",
            fontSize = 28.sp,
            color = RickAction
        )
    }
    */
    var characterState by remember { mutableStateOf<Character?>(null) }
    var episodeState by remember { mutableStateOf<List<Episode>>(emptyList()) }

    LaunchedEffect(
        key1 = Unit,
        block = {
            ktorClient.getCharacter(characterId).onSuccess { character ->
                characterState = character
                launch {
                    ktorClient.getEpisodes(character.episodeIds).onSuccess { episodes ->
                        episodeState = episodes
                    }.onFailure {
                        // TODO
                    }
                }
            }.onFailure {
                // TODO
            }
        }
    )

    characterState?.let { character ->
        MainScreen(
            character = character,
            episodes = episodeState
        )
    } ?: LoadingState()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    character: Character,
    episodes : List<Episode>
) {
    LazyColumn {
        item { CharacterNameComponent(name = character.name) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { CharacterImage(imageUrl = character.imageUrl)}

        episodes
            .groupBy { episode ->
                episode.seasonNumber
            }
            .forEach { mapEntry ->
                stickyHeader { SeasonHeader(seasonNumber = mapEntry.key) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(mapEntry.value) {episode ->
                EpisodeRowComponent(episode = episode)
            }
        }
    }
}

@Composable
private fun SeasonHeader(seasonNumber: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = RickPrimary)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Season $seasonNumber",
            color = RickTextPrimary,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = RickPrimary)
                .border(
                    width = 1.dp,
                    color = RickTextPrimary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 4.dp)
        )
    }
}