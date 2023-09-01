package n7.ad2

import android.app.AppComponentFactory
import android.app.Application
import n7.ad2.ui.MyApplication

class AD2AppComponentFactory : AppComponentFactory() {

    override fun instantiateApplication(
        classLoader: ClassLoader,
        className: String,
    ): Application = when (className) {
        MyApplication::class.java.name -> MyApplication()
        else -> super.instantiateApplication(classLoader, className)
    }

}
