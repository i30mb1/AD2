package n7.ad2.provider

import n7.ad2.heroes.api.HeroesProvider
import n7.ad2.provider.api.HeroesApi
import n7.ad2.provider.api.StreamsApi
import n7.ad2.streams.api.StreamsProvider
import n7.ad2.utils.lazyUnsafe

object AD2Provider : Provider {

    override val streamApi: StreamsApi by lazyUnsafe { StreamsProvider() }
    override val heroesApi: HeroesApi by lazyUnsafe { HeroesProvider() }

}