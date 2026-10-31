package n7.ad2.drawer.internal

import com.google.common.truth.Truth
import n7.ad2.core.ui.performance.ResourceUsage
import n7.ad2.drawer.internal.domain.PerformanceMapper
import n7.ad2.drawer.internal.domain.vo.VOPerformance
import org.junit.Test

internal class PerformanceMapperTest {

    private fun usage(cpu: Int, ram: Int, fps: Int, status: ResourceUsage.Status = ResourceUsage.Status.GOOD) = ResourceUsage(
        cpu = ResourceUsage.Info(cpu, status),
        ram = ResourceUsage.Info(ram, status),
        fps = ResourceUsage.Info(fps, status),
    )

    @Test
    fun `no history gives nothing to draw`() {
        Truth.assertThat(PerformanceMapper.map(emptyList())).isEmpty()
    }

    @Test
    fun `current values are the last sample`() {
        val result = PerformanceMapper.map(listOf(usage(10, 20, 60), usage(8, 24, 93, ResourceUsage.Status.POOR)))
        Truth.assertThat(result.map { it.kind to it.value }).containsExactly(
            VOPerformance.Kind.CPU to 8,
            VOPerformance.Kind.RAM to 24,
            VOPerformance.Kind.FPS to 93,
        ).inOrder()
        Truth.assertThat(result.first().status).isEqualTo(ResourceUsage.Status.POOR)
    }

    @Test
    fun `cpu is drawn against 100 percent, ram and fps against the highest sample`() {
        val result = PerformanceMapper.map(listOf(usage(50, 10, 30), usage(25, 20, 60))).associateBy { it.kind }
        Truth.assertThat(result.getValue(VOPerformance.Kind.CPU).points).containsExactly(0.5f, 0.25f).inOrder()
        Truth.assertThat(result.getValue(VOPerformance.Kind.RAM).points).containsExactly(0.5f, 1f).inOrder()
        Truth.assertThat(result.getValue(VOPerformance.Kind.FPS).points).containsExactly(0.5f, 1f).inOrder()
    }

    @Test
    fun `only the last samples are drawn`() {
        val history = List(100) { index -> usage(index, 1, 1) }
        val cpu = PerformanceMapper.map(history).first()
        Truth.assertThat(cpu.points).hasSize(PerformanceMapper.SAMPLES)
        Truth.assertThat(cpu.points.first()).isEqualTo((100 - PerformanceMapper.SAMPLES) / 100f)
    }
}
