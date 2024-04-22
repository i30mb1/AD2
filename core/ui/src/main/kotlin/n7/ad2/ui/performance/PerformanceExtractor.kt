package n7.ad2.ui.performance

import android.content.Context
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import n7.ad2.ktx.lazyUnsafe

interface PerformanceExtractor {
    suspend fun get(): ResourceUsage
    suspend fun release()
}

internal class PerformanceExtractorImpl(
    private val context: Context,
    private val infoMapper: UsageInfoMapper,
    private val cpuExtractor: CPUExtractor,
    private val ramExtractor: RAMExtractor,
    private val fpsExtractor: FpsExtractor,
) : PerformanceExtractor {

    private val maxFps by lazyUnsafe {
        context.display!!.refreshRate.toInt()
    }

    override suspend fun get(): ResourceUsage {
        val cpu: Deferred<ResourceUsage.Info>
        val ram: Deferred<ResourceUsage.Info>
        val fps: Deferred<ResourceUsage.Info>
        coroutineScope {
            cpu = async { getCpu() }
            ram = async { getRam() }
            fps = async { getFps() }
        }

        return ResourceUsage(
            cpu = cpu.await(),
            ram = ram.await(),
            fps = fps.await(),
        )
    }

    override suspend fun release() {

    }

    private suspend fun getCpu(): ResourceUsage.Info {
        val cpu = cpuExtractor.get()
        return infoMapper.getCpu(cpu)
    }

    private suspend fun getRam(): ResourceUsage.Info {
        val info = ramExtractor.get()
        return infoMapper.getRam(info.usedMemoryMB, info.maxMemoryMB)
    }

    private fun getFps(): ResourceUsage.Info {
        val currentFps = fpsExtractor.get()
        return infoMapper.getFps(currentFps, maxFps)
    }
}
