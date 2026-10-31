package n7.ad2.games.room.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import n7.ad2.feature.games.xo.domain.internal.server.data.Message

/**
 * Контроллеры копят все сообщения в списке состояния; отдаем только новые входящие ([Message.Other]).
 * При разрыве контроллер очищает список - тогда начинаем счет заново.
 */
internal fun <T> StateFlow<T>.newOtherMessages(messages: (T) -> List<Message>): Flow<String> = flow {
    var seen = 0
    collect { state ->
        val all = messages(state)
        if (all.size < seen) seen = 0
        all.drop(seen).filterIsInstance<Message.Other>().forEach { emit(it.text) }
        seen = all.size
    }
}
