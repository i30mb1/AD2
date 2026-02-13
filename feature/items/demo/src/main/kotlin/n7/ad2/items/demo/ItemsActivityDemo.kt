package n7.ad2.items.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import n7.ad2.items.api.ItemsFragmentFactory
import n7.ad2.items.api.ItemsProvider

internal class ItemsActivityDemo(private val fragmentFactory: ItemsFragmentFactory) : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
        val container = FragmentContainerView(this)
        container.id = View.generateViewId()
        setContentView(container)
        supportFragmentManager.commit {
            val fragment: Class<out Fragment> = ItemsProvider().getFragment()
            add(container.id, fragment, null)
        }
    }
}
