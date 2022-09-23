package ad2.n7.news.internal

import androidx.lifecycle.ViewModel
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SingleNewsViewModel @AssistedInject constructor(

) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): SingleNewsViewModel
    }

}