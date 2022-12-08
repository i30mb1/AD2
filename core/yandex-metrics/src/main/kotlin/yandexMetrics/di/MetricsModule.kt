package yandexMetrics.di

import dagger.Binds
import dagger.Module
import n7.ad2.AppMetrics
import yandexMetrics.YandexMetrics

@Module
interface MetricsModule {

    @Binds
    fun provideMetrics(yandexMetrics: YandexMetrics): AppMetrics

}