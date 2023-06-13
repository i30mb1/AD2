package yandex.metrics

import n7.ad2.common.jvm.DIComponent

interface YandexMetricComponent : DIComponent {
    val appMetrics: AppMetrics
}