package com.example.rickmortyapi.screens

import android.provider.ContactsContract.Data
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.network.ApiOperation
import com.example.network.KtorClient
import com.example.rickmortyapi.components.character.CharacterDetailsNamePlateComponent
import com.example.rickmortyapi.components.common.DataPoint
import com.example.rickmortyapi.components.common.DataPointComponent
import com.example.rickmortyapi.ui.theme.RickAction
import kotlinx.coroutines.delay
import com.example.network.models.domain.Character
import com.example.rickmortyapi.components.common.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


//Repository delle informazioni
//Tra l'altro in ordine inverso, ma va be' 2.0
class CharacterRepository @Inject constructor(private val ktorClient: KtorClient) {
    suspend fun fetchCharacter(characterId : Int) : ApiOperation<Character> {
        return ktorClient.getCharacter(characterId)
    }
}

//VM per contenere gli stati
//Brutto vizio dell'autore nell'introdurre nuove linee di codice in altri file solo per spostarli, ma va be'
@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {
    private val _internalStorageFlow = MutableStateFlow<CharacterDetailsViewState>(
        value = CharacterDetailsViewState.Loading
    )
    val stateFlow = _internalStorageFlow.asStateFlow()

    fun fetchCharacter(characterId : Int) = viewModelScope.launch {
        _internalStorageFlow.update { return@update CharacterDetailsViewState.Loading }
        characterRepository.fetchCharacter(characterId)
            .onSuccess { character ->
                val dataPoints = buildList {
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

                _internalStorageFlow.update {
                    return@update CharacterDetailsViewState.Success(
                        character = character,
                        characterDataPoints = dataPoints
                    )
                }
            }
            .onFailure { exception ->
                _internalStorageFlow.update {
                    return@update CharacterDetailsViewState.Error(
                        message = exception.message ?:"Unknown error occurred"
                    )
                }

            }

    }

}


//Gli stati stessi
//Questo è il terzo pezzo, introdotto sotto il primo come sealed interface. Mah.
sealed interface CharacterDetailsViewState {
    object Loading : CharacterDetailsViewState
    data class Error(val message : String) : CharacterDetailsViewState
    //Emula quanto succede nel Composable 'CharacterDetailsScreen'
    data class Success(
        val character: Character,
        val characterDataPoints : List <DataPoint>
    ) : CharacterDetailsViewState

 }


@Composable
fun CharacterDetailsScreen(
    //ktorClient: KtorClient,
    characterId : Int,
    viewModel : CharacterDetailsViewModel = hiltViewModel(),
    onEpisodeClicked : (Int) -> Unit
) {
    /*

    var character  by remember { mutableStateOf<Character?>(null) }

    val characterDataPoints : List<DataPoint> by remember {
        //"derivedState" cambia col cambiare del "character"
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
    */

    LaunchedEffect(
        key1 = Unit,
        block = {
            /*
            delay(500)
            character = ktorClient.getCharacter(characterId)

            val apiOperation = ktorClient.getCharacter(characterId)
            if (apiOperation is ApiOperation.Success) {


            } else if (apiOperation is ApiOperation.Failure) {

            }

            apiOperation.onSuccess {

            }.onFailure {

            }


            ktorClient
                .getCharacter(characterId)
                .onSuccess {
                    character = it
                }.onFailure { exception ->
                    // TODO handle exception
                }*/
            viewModel.fetchCharacter(characterId)
       }
    )

    val state by viewModel.stateFlow.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)
    ) {
        when(val viewState = state) {
            CharacterDetailsViewState.Loading ->
                item {
                    LoadingState()
                }

            is CharacterDetailsViewState.Error -> {
                // TODO
            }

            is CharacterDetailsViewState.Success -> {
                //NAME PLATE
                item {
                    CharacterDetailsNamePlateComponent(
                        //non null rimosso: grande successo
                        name = viewState.character.name,
                        status = viewState.character.status
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                //IMAGE
                item {
                    SubcomposeAsyncImage(
                        model = viewState.character.imageUrl,
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
                items(viewState.characterDataPoints) {
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
                                onEpisodeClicked(characterId)
                            }
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                    )
                }

                if (viewState.character == null) {
                    item {
                        LoadingState()
                    }
                    return@LazyColumn
                }

                item { Spacer(modifier = Modifier.height(64.dp)) }

            }
        }
    }
}


/*
@Composable
fun LoadingState() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxSize()
            .padding(128.dp),
        color = RickAction
    )
 }
*/