package yandex.metrics

import n7.ad2.app.logger.AdditionalLogger
import yandex.metrics.internal.YandexMetrics

@Suppress("FunctionNaming")
fun YandexMetrics(): AdditionalLogger = YandexMetrics()
