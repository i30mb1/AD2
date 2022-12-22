package n7.ad2.spanparser.di

import dagger.Module
import dagger.Provides
import n7.ad2.Resources
import n7.ad2.spanparser.AD2SpanParser
import n7.ad2.spanparser.SpanParser

@Module
class SpanParserModule {

    @Provides
    fun provideSpanParser(res: Resources): SpanParser = AD2SpanParser(res)

}