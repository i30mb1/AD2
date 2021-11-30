package n7.ad2.database_guides.internal.di

import dagger.Component
import n7.ad2.database_guides.api.DatabaseDependencies
import n7.ad2.database_guides.internal.worker.DatabaseWorker

@Component(
    dependencies = [
        DatabaseDependencies::class
    ]
)
internal interface DatabaseComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: DatabaseDependencies): DatabaseComponent
    }

    fun inject(databaseWorker: DatabaseWorker)

}