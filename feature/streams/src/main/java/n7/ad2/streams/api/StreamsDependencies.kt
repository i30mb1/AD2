package n7.ad2.streams.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.android.Dependencies
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.logger.AD2Logger
import n7.ad2.repositories.ItemRepository
import okhttp3.OkHttpClient

interface StreamsDependencies : Dependencies {
//    val itemsDao: ItemsDao
    val application: Application
    val logger: AD2Logger
    val itemRepository: ItemRepository
    val client: OkHttpClient
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}