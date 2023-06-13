@file:Suppress("UNUSED_PARAMETER")

package yandex.metrics.internal.di

import yandex.metrics.AppMetrics
import yandex.metrics.YandexMetricComponent
import yandex.metrics.internal.YandexMetrics

internal class YandexMetricComponentImpl(
    dependencies: Dependencies = Dependencies.Impl,
) : YandexMetricComponent {

    override val appMetrics: AppMetrics = YandexMetrics()
}
