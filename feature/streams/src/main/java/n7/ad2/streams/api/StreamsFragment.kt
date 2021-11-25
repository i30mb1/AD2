package n7.ad2.streams.api

import ad2.n7.android.findDependencies
import android.app.Fragment
import android.os.Bundle

class StreamsFragment : Fragment() {

    companion object {
        fun getInstance() = StreamsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val findDependencies: StreamsDependencies = findDependencies()
        findDependencies
//        if (isInstantApp(this)) {
//        if (true) {
//            Intent(Intent.ACTION_MAIN).apply {
//                addCategory(Intent.CATEGORY_DEFAULT)
//                setPackage("ad2.n7.feature_streams")
//                InstantApps.showInstallPrompt(this@StreamsActivity, this, 7, null)
//            }
//        }

    }
}
