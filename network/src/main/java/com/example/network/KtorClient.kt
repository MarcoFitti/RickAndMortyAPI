package com.example.network

import com.example.network.models.domain.Character
import com.example.network.models.domain.CharacterPage
import com.example.network.models.domain.Episode
import com.example.network.models.domain.EpisodePage
import com.example.network.models.remote.RemoteCharacter
import com.example.network.models.remote.RemoteCharacterPage
import com.example.network.models.remote.RemoteEpisode
import com.example.network.models.remote.RemoteEpisodePage
import com.example.network.models.remote.toDomainCharacter
import com.example.network.models.remote.toDomainCharacterPage
import com.example.network.models.remote.toDomainEpisode
import com.example.network.models.remote.toDomainEpisodePage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KtorClient {
    private val client = HttpClient(OkHttp) {
        defaultRequest { url("https://rickandmortyapi.com/api/") }

        install(Logging) {
            logger = Logger.SIMPLE
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    /*
    CACHING: una volta svolte delle chiamate di rete, permette di salvare delle informazioni
    per prevenire la necessità di dover svolgere nuove chiamate per dati già acquisiti recentemente
    In questo es.: salviamo le informazioni principali del personaggio per non doverle richiamare
    ogni volta che scorriamo dalla loro  schermata a quella degli episodi
    */
    private var characterCache = mutableMapOf<Int, Character>()


    suspend fun getCharacter(id: Int): ApiOperation<Character> {
        characterCache[id]?.let { return ApiOperation.Success(it) }
        return safeApiCall {
            client.get("character/$id")
                .body<RemoteCharacter>()
                .toDomainCharacter()
                .also { characterCache[id] = it }
        }
    }

    suspend fun getEpisode(episodeId: Int): ApiOperation<Episode> {
        return safeApiCall {
            client.get("episode/$episodeId")
                .body<RemoteEpisode>()
                .toDomainEpisode()
        }

    }

    suspend fun getEpisodes(episodeIds: List<Int>): ApiOperation<List<Episode>> {
        return if (episodeIds.size == 1) {
            //Tutto questo ambaradam trasforma un oggetto in una lista con un solo elemento
            getEpisode(episodeIds[0]).mapSuccess { episode ->
                listOf(episode)
            }
        } else {
            val idsCommaSeparated = episodeIds.joinToString(separator = ",")
            safeApiCall {
                client
                    .get("episode/$idsCommaSeparated")
                    .body<List<RemoteEpisode>>()
                    .map { remoteEpisode ->
                        remoteEpisode.toDomainEpisode()
                    }
            }
        }
    }

    suspend fun getCharacterByPage(
        pageNumber: Int,
        queryParams : Map<String, String>
    ): ApiOperation<CharacterPage> {
        return safeApiCall {
            client.get("character") {
                url {
                    parameters.append("page", pageNumber.toString())
                    queryParams.forEach { parameters.append(it.key, it.value) }
                }
            }
                .body<RemoteCharacterPage>()
                .toDomainCharacterPage()
        }
    }

    suspend fun searchAllCharactersByName(searchQuery : String) : ApiOperation<List<Character>> {
        val data = mutableListOf<Character>()
        var exception : Exception? = null

        getCharacterByPage(
            pageNumber = 1,
            queryParams = mapOf("name" to searchQuery)
        ).onSuccess { firstPage ->
            val totalPageCount = firstPage.info.pages
            data.addAll(firstPage.characters)

            repeat(totalPageCount -1) { index ->
                getCharacterByPage(
                    pageNumber = index +2,
                    queryParams = mapOf("name" to searchQuery)
                ).onSuccess { nextPage ->
                    data.addAll(nextPage.characters)
                }.onFailure { error ->
                    exception = error
                }

                if (exception == null) { return@onSuccess }
            }
        }.onFailure {
            exception = it }

        return exception?.let {ApiOperation.Failure(it) } ?: ApiOperation.Success(data)

    }


    suspend fun getEpisodesByPage(pageIndex: Int): ApiOperation<EpisodePage> {
        return safeApiCall {
            client.get("episode") {
                url {
                    parameters.append("page", pageIndex.toString())
                }
            }
                .body<RemoteEpisodePage>()
                .toDomainEpisodePage()
        }
    }

    suspend fun getAllEpisodes(): ApiOperation<List<Episode>> {
        val data = mutableListOf<Episode>()
        var exception: Exception? = null

        getEpisodesByPage(pageIndex = 1).onSuccess { firstPage ->
            val totalPageCount = firstPage.info.pages
            data.addAll(firstPage.episodes)

            //Un'altra volta, problemi per omissioni nel video: dava errore in "getEpisodesByPage" perché la funzione "onSuccess" non era suspend
            //Leggendo i commenti nel video ho trovato qualcuno che ha fatto l'osservazione al cambiamento del codice
            repeat(totalPageCount - 1) { index ->
                getEpisodesByPage(pageIndex = index + 2).onSuccess { nextPage ->
                    data.addAll(nextPage.episodes)
                }.onFailure { error ->
                    exception = error
                }

                if (exception == null) { return@onSuccess }
            }
        }.onFailure {
            exception = it
        }

        return exception?.let { ApiOperation.Failure(it) } ?: ApiOperation.Success(data)
    }


    private inline fun <T> safeApiCall(apiCall: () -> T): ApiOperation<T> {
        return try {
            ApiOperation.Success(data = apiCall())
        } catch (e: Exception) {
            ApiOperation.Failure(exception = e)
        }
    }
}


sealed interface ApiOperation<T> {
    //Definiamo due casi: successo...
    data class Success<T>(val data: T) : ApiOperation<T>

    //.. e fallimento
    data class Failure<T>(val exception: Exception) : ApiOperation<T>

    fun <R> mapSuccess(transform: (T) -> R): ApiOperation<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Failure -> Failure(exception)
        }
    }



    suspend fun onSuccess(block: suspend (T) -> Unit): ApiOperation<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): ApiOperation<T> {
        if (this is Failure) block(exception)
        return this
    }
}