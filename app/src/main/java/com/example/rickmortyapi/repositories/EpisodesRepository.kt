package com.example.rickmortyapi.repositories

import androidx.lifecycle.ViewModel
import com.example.network.ApiOperation
import com.example.network.KtorClient
import com.example.network.models.domain.Episode
import javax.inject.Inject

class EpisodesRepository @Inject constructor(private val ktorClient : KtorClient)  {
    suspend fun fetchAllEpisodes() : ApiOperation<List<Episode>> = ktorClient.getAllEpisodes()

}