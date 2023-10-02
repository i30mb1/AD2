package n7.ad2.items.domain.di

import n7.ad2.items.domain.internal.usecase.GetArticleUseCaseImpl
import n7.ad2.items.domain.internal.usecase.GetNewsUseCaseImpl
import n7.ad2.items.domain.usecase.GetArticleUseCase
import n7.ad2.items.domain.usecase.GetNewsUseCase

interface NewsDomainComponent {

    val getNewsUseCase: GetNewsUseCase
    val getArticleUseCase: GetArticleUseCase
}

fun NewsDomainComponent(
    dependencies: NewsDomainDependencies,
): NewsDomainComponent = object : NewsDomainComponent {

    override val getNewsUseCase = GetNewsUseCaseImpl(
        dependencies.logger,
        dependencies.appInformation,
        dependencies.dispatcher,
    )
    override val getArticleUseCase = GetArticleUseCaseImpl(
        dependencies.newsDao,
        dependencies.dispatcher,
    )
}
