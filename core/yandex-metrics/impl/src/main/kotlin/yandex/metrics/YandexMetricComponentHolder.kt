package yandex.metrics

import n7.ad2.common.jvm.ComponentHolder
import yandex.metrics.internal.di.Dependencies
import yandex.metrics.internal.di.YandexMetricComponentImpl

object YandexMetricComponentHolder : ComponentHolder<YandexMetricComponent>() {

    override fun build(): YandexMetricComponent {
        return YandexMetricComponentImpl(Dependencies.Impl)
    }
}
