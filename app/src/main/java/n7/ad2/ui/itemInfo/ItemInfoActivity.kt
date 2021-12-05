package n7.ad2.ui.itemInfo

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import n7.ad2.R
import n7.ad2.databinding.ActivityItemInfoBinding

class ItemInfoActivity : FragmentActivity() {

    lateinit var binding: ActivityItemInfoBinding

    companion object {
        const val ITEM_NAME = "ITEM_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val itemName = intent.getStringExtra(ITEM_NAME)!!

        supportFragmentManager.commit {
            replace(R.id.fragment, ItemInfoFragment.newInstance(itemName))
        }
    }
}