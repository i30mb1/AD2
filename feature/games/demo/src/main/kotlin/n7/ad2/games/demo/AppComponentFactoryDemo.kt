package n7.ad2.games.demo

import android.app.Activity
import android.app.AppComponentFactory
import android.content.Intent
import android.util.Log


internal class AppComponentFactoryDemo : AppComponentFactory() {

    override fun instantiateActivity(
        cl: ClassLoader,
        className: String,
        intent: Intent?,
    ): Activity = when (className) {
        GamesActivityDemo::class.java.name -> GamesActivityDemo { message ->
            Log.d("N7", message)
        }

        else -> super.instantiateActivity(cl, className, intent)
    }
}
