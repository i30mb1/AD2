package yandexmetrics.di

import n7.ad2.AppMetrics
import yandexmetrics.YandexMetrics

@dagger.Module
interface MetricsModule {

    @dagger.Binds
    fun provideMetrics(yandexMetrics: YandexMetrics): AppMetrics

}