@file:Suppress("UNCHECKED_CAST")

package n7.ad2.utils

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import n7.ad2.ui.itemInfo.ItemInfoViewModel

inline fun <reified T : ViewModel> FragmentActivity.viewModel(
        crossinline provider: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = provider() as T
    }
}

inline fun <reified T : ViewModel> ComponentActivity.viewModel(
        crossinline provider: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = provider() as T
    }
}

inline fun <reified T : ViewModel> Fragment.viewModel(
        crossinline provider: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = provider() as T
    }
}

inline fun <reified T : ViewModel> Fragment.activityViewModel(
        crossinline provider: () -> T
) = activityViewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = provider() as T
    }
}

interface AssistedFactory<T : ViewModel, Param1> {
    fun create(handle: Param1): T
}

/* Please work */
inline fun <reified T : ViewModel, Param1> Fragment.viewModelWithParam(
        defaultArgs: Bundle? = null,
        param1: Param1,
        crossinline provider: () -> AssistedFactory<T, Param1>,
) = viewModels<T> {
    object : AbstractSavedStateViewModelFactory(this, defaultArgs) {
        override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle,
        ): T {
            return provider().create(param1) as T
        }
    }
}
