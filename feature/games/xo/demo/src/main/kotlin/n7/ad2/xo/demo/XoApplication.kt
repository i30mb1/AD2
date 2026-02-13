package n7.ad2.xo.demo

import android.app.Application

internal class XoApplication(private val application: (component: Application) -> Unit) : Application() {

    override fun onCreate() {
        application(this)
        super.onCreate()
    }
}
