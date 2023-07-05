package n7.ad2.heroes.domain.di

import com.squareup.moshi.Moshi
import n7.ad2.Resources
import n7.ad2.database_guides.api.dao.HeroesDao

interface HeroesDomainDependencies {
    val res: Resources
    val heroesDao: HeroesDao
    val moshi: Moshi
}
