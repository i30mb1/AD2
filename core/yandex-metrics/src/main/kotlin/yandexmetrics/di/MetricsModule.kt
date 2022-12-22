package yandexmetrics.di

import dagger.Binds
import dagger.Module
import n7.ad2.AppMetrics
import yandexmetrics.YandexMetrics

@Module
interface MetricsModule {

    @Binds
    fun provideMetrics(yandexMetrics: YandexMetrics): AppMetrics

}