package com.example.rickmortyapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.network.models.domain.Character
import com.example.network.KtorClient
import com.example.network.TestFile
import com.example.rickmortyapi.screens.CharacterDetailsScreen
import com.example.rickmortyapi.ui.theme.RickAction
import com.example.rickmortyapi.ui.theme.RickMortyAPITheme
import com.example.rickmortyapi.ui.theme.RickPrimary
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            /*
            var character by remember {
                mutableStateOf<Character?>(null)
            }


            LaunchedEffect(
                key1 = Unit,
                block = {
                    delay(3000)
                    character = ktorClient.getCharacter(12)
                }
            )
            */

            val navController = rememberNavController()

            RickMortyAPITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RickPrimary
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "character_details"
                    ) {
                        composable(route = "character_details")  {
                            CharacterDetailsScreen(
                                ktorClient = ktorClient,
                                characterId = 1
                            ) {
                                navController.navigate(route = "character_episodes/$it")
                            }
                        }
                        composable(
                            route = "character_episodes/{characterId}",
                            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val characterId: Int = backStackEntry.arguments?.getInt("characterId") ?: -1
                            CharacterEpisodeScreen(characterId = characterId)
                        }
                    }
                    /*
                    CharacterDetailsScreen(
                        ktorClient = ktorClient,
                        characterId = 1
                    )

                    */
                }

            }
        }
    }
}

@Composable
fun CharacterEpisodeScreen(characterId : Int) {
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
}
