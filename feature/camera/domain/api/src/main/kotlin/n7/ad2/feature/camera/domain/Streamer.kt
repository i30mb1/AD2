package n7.ad2.feature.camera.domain

import kotlinx.coroutines.flow.Flow
import n7.ad2.feature.camera.domain.model.StreamerState

/**
 * Сущность отвечающая за раздачу кадров с камеры
 * Для запуска -> подписаться
 * Для прекращения -> отписаться
 */
interface Streamer {
    val stream: Flow<StreamerState>
    fun start(): Any
}

data class StreamerResolution(val width: Int, val height: Int)
