package n7.ad2.games.room.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Комната вызывается с главного потока (кнопки, ответы), а запись в сокет там запрещена (NetworkOnMainThreadException).
 * Очередь выполняет действия с сокетом по одному на IO, сохраняя порядок: закрытие после отправки уходит после нее;
 * ошибка записи не роняет приложение - разрыв увидит статус транспорта.
 */
internal class OutgoingQueue(scope: CoroutineScope) {

    private val channel = Channel<() -> Unit>(Channel.UNLIMITED)

    init {
        scope.launch(Dispatchers.IO) {
            for (action in channel) runCatching { action() }
        }
    }

    fun post(action: () -> Unit) {
        channel.trySend(action)
    }
}
