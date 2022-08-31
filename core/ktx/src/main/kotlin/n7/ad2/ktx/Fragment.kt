@file:Suppress("UNCHECKED_CAST", "WRONG_NULLABILITY_FOR_JAVA_OVERRIDE")

package n7.ad2.ktx

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

inline fun <reified T : ViewModel> Fragment.viewModel(
    defaultArgs: Bundle? = null,
    crossinline provider: (handle: SavedStateHandle) -> T,
) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this, defaultArgs) {
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return provider.invoke(handle) as T
        }
    }
}