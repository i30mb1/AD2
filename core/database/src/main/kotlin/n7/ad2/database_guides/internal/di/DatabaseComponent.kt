package n7.ad2.database_guides.internal.di

import dagger.Component
import n7.ad2.database_guides.api.DatabaseDependencies

@Component(
    dependencies = [
        DatabaseDependencies::class,
    ],
)
interface DatabaseComponent {

    @Component.Factory
    interface Factory {
        fun create(dependencies: DatabaseDependencies): DatabaseComponent
    }
}
