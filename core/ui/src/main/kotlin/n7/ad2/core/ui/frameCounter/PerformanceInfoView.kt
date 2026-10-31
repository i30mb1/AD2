package n7.ad2.core.ui.frameCounter

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.core.ui.performance.PollerImpl

/**
 * CPU / RAM / FPS панель, которая живёт внутри иерархии view (а не отдельным окном поверх всего).
 * Кладём её под контент, и она видна только когда контент сдвинут (например, открыт drawer).
 */
class PerformanceInfoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val infoPanelView = InfoPanelView(context)
    private val mapper = PanelInfoMapper()
    private var job: Job? = null

    init {
        addView(infoPanelView)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val owner = findViewTreeLifecycleOwner() ?: return
        val poller = PollerImpl(context, owner.lifecycle)
        job = poller.usage
            .onEach { list -> infoPanelView.render(mapper.map(list.last())) }
            .launchIn(owner.lifecycleScope)
    }

    override fun onDetachedFromWindow() {
        job?.cancel()
        job = null
        super.onDetachedFromWindow()
    }
}
