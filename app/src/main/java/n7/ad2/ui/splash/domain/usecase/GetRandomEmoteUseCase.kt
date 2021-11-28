package n7.ad2.ui.splash.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetRandomEmoteUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(): String = withContext(dispatchers.Default) {
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