package n7.ad2.camera.demo

import android.app.Application

internal class CameraApplication(private val application: (component: Application) -> Unit) : Application() {

    override fun onCreate() {
        application(this)
        super.onCreate()
    }
}
