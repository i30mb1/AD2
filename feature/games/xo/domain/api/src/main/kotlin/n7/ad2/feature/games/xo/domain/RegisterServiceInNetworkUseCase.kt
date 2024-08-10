package n7.ad2.feature.games.xo.domain

import n7.ad2.feature.games.xo.domain.model.SimpleServer

interface RegisterServiceInNetworkUseCase {
    /**
     * Возвращает зафиналеную версию обьекта Server
     * т.к. в некоторых случаях имя сервера или порт может быть уже использован
     */
    suspend fun register(server: SimpleServer): SimpleServer
    fun unregister()
}
