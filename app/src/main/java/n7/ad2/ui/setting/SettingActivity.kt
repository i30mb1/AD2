package n7.ad2.ui.setting

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import n7.ad2.R
import n7.ad2.databinding.ActivitySettingBinding
import n7.ad2.utils.BaseActivity

class SettingActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        supportFragmentManager.commit {
            replace(binding.container.id, SettingsFragment())
        }
        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbarActivitySetting)
        supportActionBar?.let {
            it.setTitle(R.string.setting)
            it.setDisplayShowHomeEnabled(true) //включаем кнопку домой
            it.setDisplayHomeAsUpEnabled(true) //ставим значок стрелочки на кнопку
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}