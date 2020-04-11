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
                "(=_=)",
                "('x')",
                "(=^・^=)",
                "(＠_＠;)",
                "=^_^=",
                ">^_^<",
                "(=^・・^=)",
                "(／ロ°)／",
                "＼(°ロ＼)",
                "(>_<)",
                "(>.<)",
                "_(._.)_",
                "(^_^)/",
                "_(_^_)_",
                "(·ω·)",
                "(/◕ヮ◕)/",
                "(^o^)／",
                "(>_<)>",
                "(°.°)",
                "(°o°)",
                "(°_°)",
                "<(｀^´)>",
                "(°-°)",
                "(°°)",
                "((+_+))",
                "(^_-)",
                "(;-;)",
                "\\(o_o)/",
                "(O_o)",
                "(o_0)",
                "(≥o≤)",
                "(-_-)zzz",
                "(・_・;)",
                "(#^.^#)",
                "(~_~;)",
                "(-_-;)",
                "ヽ(´ー｀)ﾉ",
                "(·.·)",
                "◉_◉",
                "٩◔̯◔۶",
                "(/) (°,,°) (/)",
                "ヽ(ｏ`皿′ｏ)ﾉ",
                "¯\\_(ツ)_/¯",
                "<`～´>",
                "(✿◠‿◠)",
                "(ーー;)",
                "(-_-メ)",
                "<`ヘ´>"
        )
        emotes.random()
    }
}