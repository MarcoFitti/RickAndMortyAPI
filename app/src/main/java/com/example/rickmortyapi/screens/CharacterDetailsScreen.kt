package com.example.rickmortyapi.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.network.KtorClient
import com.example.rickmortyapi.components.CharacterDetailsNamePlateComponent
import com.example.rickmortyapi.components.common.DataPoint
import com.example.rickmortyapi.components.common.DataPointComponent
import com.example.rickmortyapi.components.common.LoadingState
import com.example.rickmortyapi.ui.theme.RickAction
import kotlinx.coroutines.delay
import com.example.network.models.domain.Character


@Composable
fun CharacterDetailsScreen(
    ktorClient: KtorClient,
    characterId : Int,
   //onEpisodeClicked : (Int) -> Unit
) {

    var character by remember { mutableStateOf<Character?>(null) }

    val characterDataPoints: List<DataPoint> by remember {
        //derivedState cambia col cambiare del "character"
        derivedStateOf {
            buildList {
                character?.let { character  ->
                    add(DataPoint("Last known location", character.location.name))
                    add(DataPoint("Species", character.species))
                    add(DataPoint("Gender", character.gender.displayName))
                    character.type.takeIf {
                        it.isNotEmpty()
                    }?.let { type ->
                        add(DataPoint("Type", type))
                    }
                    add(DataPoint("Origin", character.origin.name))
                    add(DataPoint("Episode count", character.episodeIds.size.toString()))

                }
            }
        }
    }

    LaunchedEffect(
        key1 = Unit,
        block = {
            delay(500)
            character = ktorClient.getCharacter(characterId)
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)
    ) {
        if (character == null) {
            item { LoadingState() }
            return@LazyColumn
        }

        //NAME PLATE
        item {
            CharacterDetailsNamePlateComponent(
                name = character!!.name,
                status = character!!.status
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }


        //IMAGE
        item {
            SubcomposeAsyncImage(
                //!! = variabile non sarà mai null. rischioso, perché se è null crasha l'app
                model = character!!.imageUrl,
                contentDescription = "Character image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1F)
                    .clip(RoundedCornerShape(12.dp)),
                loading = {
                    LoadingState()
                }
            )
        }

        //DATA POINTS
        items(characterDataPoints) {
            Spacer(modifier = Modifier.height(32.dp))
            DataPointComponent(dataPoint = it)
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }

        //BUTTON
        item {
            Text(
                text = "View all episode",
                color = RickAction,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .border(
                        width = 1.dp,
                        color = RickAction,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        // TODO onEpisodeClicked(characterId)
                    }
                    .padding(vertical = 8.dp)

            )
        }

        item { Spacer(modifier = Modifier.height(64.dp)) }
    }

}