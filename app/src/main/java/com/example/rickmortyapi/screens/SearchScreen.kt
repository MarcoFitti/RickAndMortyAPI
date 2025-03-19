package com.example.rickmortyapi.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickmortyapi.components.character.CharacterListItem
import com.example.rickmortyapi.components.common.DataPoint
import com.example.rickmortyapi.components.common.SimpleToolbar
import com.example.rickmortyapi.ui.theme.RickAction
import com.example.rickmortyapi.ui.theme.RickPrimary
import com.example.rickmortyapi.viewmodels.SearchViewModel


@Composable
fun SearchScreen(searchViewModel : SearchViewModel = hiltViewModel()) {

    DisposableEffect(key1 = Unit) {
        val job = searchViewModel.observeUserSearch()
        onDispose { job.cancel() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleToolbar(title = "Search")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search Icon",
                    tint = RickPrimary
                )

                BasicTextField(
                    state = searchViewModel.searchTextFieldState,
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(visible = searchViewModel.searchTextFieldState.text.isNotBlank()) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete Icon",
                    tint = RickAction,
                    modifier = Modifier.clickable {
                        searchViewModel.searchTextFieldState.edit { delete(0, length) }
                    }
                )
            }

        }
        val screenState by searchViewModel.uiState.collectAsStateWithLifecycle()

        when (val state = screenState) {
            SearchViewModel.ScreenState.Empty -> {
                Text(
                    text = "Search for characters!",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp
                )
            }

            SearchViewModel.ScreenState.Searching -> { }

            is SearchViewModel.ScreenState.Error -> {
                Text(
                    text = state.message,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp
                )
            }

            is SearchViewModel.ScreenState.Content -> SearchScreenContent(content = state)
        }
    }
}


@Composable
private fun SearchScreenContent(content : SearchViewModel.ScreenState.Content) {
    LazyColumn {
        items(content.results) { character ->
            val dataPoints = buildList {
                add(DataPoint("Last known location", character.location.name))
                add(DataPoint("Species", character.species))
                add(DataPoint("Gender", character.gender.displayName))
                character.type.takeIf { it.isNotEmpty() }?.let { type ->
                    add(DataPoint("Type", type))
                }
                add(DataPoint("Origin", character.origin.name))
                add(DataPoint("Episode count", character.episodeIds.size.toString()))
            }

            CharacterListItem(
                character = character,
                characterDataPoints = dataPoints,
                onClick = {             }
            )
        }
    }

}
