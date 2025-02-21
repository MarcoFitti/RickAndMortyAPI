package com.example.rickmortyapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.network.models.domain.Character
import com.example.network.KtorClient
import com.example.network.TestFile
import com.example.rickmortyapi.screens.CharacterDetailsScreen
import com.example.rickmortyapi.ui.theme.RickMortyAPITheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val ktorClient = KtorClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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


            RickMortyAPITheme {
                CharacterDetailsScreen(ktorClient, 12)

            }
        }
    }
}

