package n7.ad2.span_parser.di

import dagger.Module
import dagger.Provides
import n7.ad2.Resources
import n7.ad2.span_parser.AD2SpanParser
import n7.ad2.span_parser.SpanParser

@Module
class SpanParserModule {

    @Provides
    fun provideSpanParser(res: Resources): SpanParser = AD2SpanParser(res)

}