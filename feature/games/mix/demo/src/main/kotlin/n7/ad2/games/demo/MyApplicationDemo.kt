package n7.ad2.games.demo

import android.app.Application

internal class MyApplicationDemo(
    private val application: (component: Application) -> Unit,
) : Application() {

    override fun onCreate() {
        application(this)
        super.onCreate()
    }
}
