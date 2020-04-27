package ad2.n7.feature_streams

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class StreamsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(n7.ad2.R.style.AD2Theme_White)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)

        val action: String? = intent?.action
        val data: Uri? = intent?.data

//        if (isInstantApp(this)) {
//        if (true) {
//            Intent(Intent.ACTION_MAIN).apply {
//                addCategory(Intent.CATEGORY_DEFAULT)
//                setPackage("ad2.n7.feature_streams")
//                InstantApps.showInstallPrompt(this@StreamsActivity, this, 7, null)
//            }
//        }

        Log.d("N7", action.toString())
        Log.d("N7", data.toString())

    }
}
