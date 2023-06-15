package yandex.metrics

import yandex.metrics.internal.YandexMetricsImpl

fun YandexMetrics(): YandexMetrics {
    return YandexMetricsImpl()
}
