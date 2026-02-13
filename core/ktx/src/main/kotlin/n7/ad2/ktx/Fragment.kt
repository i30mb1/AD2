@file:Suppress("UNCHECKED_CAST")

package n7.ad2.ktx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

inline fun <reified T : ViewModel> Fragment.viewModel(defaultArgs: Bundle? = null, crossinline provider: (handle: SavedStateHandle) -> T) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this@viewModel, defaultArgs) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T = provider.invoke(handle) as T
    }
}

inline fun <reified T : ViewModel> ComponentActivity.viewModel(defaultArgs: Bundle? = null, crossinline provider: (handle: SavedStateHandle) -> T) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this@viewModel, defaultArgs) {
        override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T = provider.invoke(handle) as T
    }
}
