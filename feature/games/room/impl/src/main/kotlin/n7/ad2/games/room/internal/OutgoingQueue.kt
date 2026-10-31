package n7.ad2.games.room.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Комната вызывается с главного потока (кнопки, ответы), а запись в сокет там запрещена (NetworkOnMainThreadException).
 * Очередь отправляет сообщения по одному на IO, сохраняя порядок; ошибка записи не роняет приложение -
 * разрыв увидит статус транспорта.
 */
internal class OutgoingQueue(scope: CoroutineScope, private val write: (String) -> Unit) {

    private val channel = Channel<String>(Channel.UNLIMITED)

    init {
        scope.launch(Dispatchers.IO) {
            for (text in channel) runCatching { write(text) }
        }
    }

    fun send(text: String) {
        channel.trySend(text)
    }
}
