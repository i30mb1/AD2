package n7.ad2.ui.performance

/**
 * Преобразовывает значение в обьект с уровнем качества
 */
internal class UsageInfoMapper {

    fun getCpu(cpu: Int): ResourceUsage.Info {
        val value = cpu.toFloat()
        val status = value.getQuality(90f, 20f)
        return ResourceUsage.Info(cpu, status)
    }

    fun getRam(used: Long, total: Long): ResourceUsage.Info {
        val value = used.toFloat() / total.toFloat()
        val status = value.getQuality(0.9f, 0.5f)
        return ResourceUsage.Info(used.toInt(), status)
    }

    fun getFps(currentFps: Int?, maxFps: Float): ResourceUsage.Info {
        val fps = currentFps ?: maxFps.toInt()
        val value = fps / maxFps
        val status = value.getQuality(0.5f, 0.9f)
        return ResourceUsage.Info(fps.coerceIn(0, maxFps.toInt()), status)
    }

    /**
     * @return возвращает статус из определенного диапазона
     */
    private fun Float.getQuality(from: Float, to: Float): ResourceUsage.Status {
        val status = ResourceUsage.Status.entries.toTypedArray()
        val index = ((this - from) * status.size / (to - from)).toInt().coerceIn(0, status.lastIndex)
        return status[index]
    }
}