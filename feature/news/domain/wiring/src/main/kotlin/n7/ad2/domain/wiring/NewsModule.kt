package n7.ad2.domain.wiring

import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.dao.NewsDao
import n7.ad2.items.domain.di.NewsDomainComponent
import n7.ad2.items.domain.di.NewsDomainDependencies
import n7.ad2.items.domain.usecase.GetArticleUseCase
import n7.ad2.items.domain.usecase.GetNewsUseCase

@dagger.Module
object NewsModule {

    @dagger.Provides
    fun provideNewsDomainComponent(newsDao: NewsDao, logger: Logger, res: Resources, appInformation: AppInformation, dispatcher: DispatchersProvider): NewsDomainComponent = NewsDomainComponent(
        object : NewsDomainDependencies {
            override val newsDao: NewsDao = newsDao
            override val logger: Logger = logger
            override val res: Resources = res
            override val appInformation: AppInformation = appInformation
            override val dispatcher: DispatchersProvider = dispatcher
        },
    )

    @dagger.Provides
    fun provideGetArticleUseCase(component: NewsDomainComponent): GetArticleUseCase = component.getArticleUseCase

    @dagger.Provides
    fun provideGetNewsUseCase(component: NewsDomainComponent): GetNewsUseCase = component.getNewsUseCase
}
