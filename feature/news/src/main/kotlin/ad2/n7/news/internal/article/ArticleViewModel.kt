package ad2.n7.news.internal.article

import androidx.lifecycle.ViewModel
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ArticleViewModel @AssistedInject constructor(

) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): ArticleViewModel
    }

}