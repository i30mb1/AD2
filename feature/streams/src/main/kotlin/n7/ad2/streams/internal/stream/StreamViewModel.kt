package n7.ad2.streams.internal.stream

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import n7.ad2.android.AD2ErrorMessage
import n7.ad2.android.ErrorMessage
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.streams.internal.chat.ChatMessage
import n7.ad2.streams.internal.chat.TwitchChat
import n7.ad2.streams.internal.data.remote.StreamRepository
import n7.ad2.streams.internal.domain.ConvertStreamToVOStreamUseCase
import n7.ad2.streams.internal.domain.vo.VOStream
import n7.ad2.streams.internal.model.ChatDensity
import n7.ad2.streams.internal.stream.usecase.GetStreamUrlsUseCase

private const val PREFERENCES = "streams"
private const val KEY_CHAT_DENSITY = "chat_density"
private const val INFO_REFRESH_MILLIS = 60_000L
private const val CHAT_RETRY_MAX_MILLIS = 30_000L
private const val CHAT_SUBSCRIPTION_TIMEOUT_MILLIS = 5_000L

internal class StreamViewModel @AssistedInject constructor(
    application: Application,
    private val getStreamUrlsUseCase: GetStreamUrlsUseCase,
    private val streamRepository: StreamRepository,
    private val convertStreamToVOStreamUseCase: ConvertStreamToVOStreamUseCase,
    private val twitchChat: TwitchChat,
    private val dispatchers: DispatchersProvider,
) : ViewModel(),
    ErrorMessage by AD2ErrorMessage() {

    @AssistedFactory
    interface Factory {
        fun create(): StreamViewModel
    }

    private val preferences = application.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private val channel = MutableStateFlow<String?>(null)
    private var infoJob: Job? = null

    val url: MutableStateFlow<String> = MutableStateFlow("")

    /** Name, title and viewers of the stream, null until Twitch answers or when the channel is offline. */
    private val _info = MutableStateFlow<VOStream.Simple?>(null)
    val info: StateFlow<VOStream.Simple?> = _info.asStateFlow()

    private val _chatDensity = MutableStateFlow(readChatDensity())
    val chatDensity: StateFlow<ChatDensity> = _chatDensity.asStateFlow()

    /** Messages of the channel's chat, the connection is restored with a growing pause when it drops. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val chat: Flow<ChatMessage> = channel
        .filterNotNull()
        .flatMapLatest { name ->
            twitchChat.messages(name)
                .retryWhen { _, attempt ->
                    delay((1_000L shl attempt.toInt().coerceAtMost(5)).coerceAtMost(CHAT_RETRY_MAX_MILLIS))
                    true
                }
        }
        .flowOn(dispatchers.IO)
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(CHAT_SUBSCRIPTION_TIMEOUT_MILLIS))

    fun load(streamerName: String) {
        channel.value = streamerName
        _info.value = null
        getStreamUrlsUseCase(streamerName)
            .catch { throwable -> showError(throwable) }
            .onEach { url.value = it }
            .launchIn(viewModelScope)
        infoJob?.cancel()
        infoJob = viewModelScope.launch {
            // the viewers count changes all the time, Twitch updates it about once a minute
            while (isActive) {
                runCatching { streamRepository.getStream(streamerName) }
                    .onSuccess { stream -> _info.value = stream?.let(convertStreamToVOStreamUseCase::invoke) as? VOStream.Simple }
                delay(INFO_REFRESH_MILLIS)
            }
        }
    }

    fun setChatDensity(density: ChatDensity) {
        _chatDensity.value = density
        preferences.edit { putString(KEY_CHAT_DENSITY, density.name) }
    }

    private fun readChatDensity(): ChatDensity = ChatDensity.entries.find { it.name == preferences.getString(KEY_CHAT_DENSITY, null) } ?: ChatDensity.NORMAL
}
