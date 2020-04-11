package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRandomEmoteUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(): String = withContext(ioDispatcher) {
        val emotes = arrayOf(
                "('.')",
                "('x')",
                "(>_<)",
                "(>.<)",
                "(;-;)",
                "\\(o_o)/",
                "(O_o)",
                "(o_0)",
                "(≥o≤)",
                "(≥o≤)",
                "(·.·)",
                "(·_·)"
        )
        emotes.random()
    }
}