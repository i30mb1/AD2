package n7.ad2.ui.itemInfo

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import n7.ad2.R
import n7.ad2.databinding.ActivityItemInfoBinding
import n7.ad2.utils.BaseActivity

class ItemInfoActivity : BaseActivity() {

    lateinit var binding: ActivityItemInfoBinding

    companion object {
        const val ITEM_NAME = "ITEM_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_info)

        val itemName = intent.getStringExtra(ITEM_NAME)!!

        supportFragmentManager.commit {
            replace(R.id.fragment, ItemInfoFragment.newInstance(itemName))
        }
    }
}