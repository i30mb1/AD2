package n7.ad2.spanparser.di

import n7.ad2.Resources
import n7.ad2.spanparser.AD2SpanParser
import n7.ad2.spanparser.SpanParser

@dagger.Module
object SpanParserModule {

    @dagger.Provides
    fun provideSpanParser(res: Resources): SpanParser = AD2SpanParser(res)
}
