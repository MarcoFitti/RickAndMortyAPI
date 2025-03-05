package com.example.rickmortyapi.repositories

import com.example.network.ApiOperation
import com.example.network.KtorClient
import com.example.network.models.domain.Character
import com.example.network.models.domain.CharacterPage
import javax.inject.Inject

class CharacterRepository @Inject constructor(private val ktorClient : KtorClient) {
    suspend fun fetchCharacterPage(page : Int) : ApiOperation<CharacterPage> {
        return ktorClient.getCharacterByPage(pageNumber = page)
    }

    suspend fun fetchCharacter(characterId : Int) : ApiOperation<Character> {
        return ktorClient.getCharacter(characterId)
    }
}