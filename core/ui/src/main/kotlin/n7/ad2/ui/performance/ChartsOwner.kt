package n7.ad2.ui.performance

import android.view.LayoutInflater
import kotlinx.coroutines.withContext
import n7.ad2.core.ui.databinding.ChartViewBinding
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.ui.frameCounter.WidgetContainerView

class ChartsOwner(
    private val container: WidgetContainerView,
    private val dispatcher: DispatchersProvider,
) {

    private val mapper = LineChartMapper(dispatcher)
    private val binding: ChartViewBinding = ChartViewBinding.inflate(
        LayoutInflater.from(container.context),
        container,
        true
    )

    suspend fun render(list: List<ResourceUsage>) {
        withContext(dispatcher.Main) {

            val (cpu, ram, fps) = mapper.map(list)
            binding.cpu.render(cpu)
            binding.ram.render(ram)
            binding.fps.render(fps)
        }
    }
}