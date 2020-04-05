package n7.ad2.ui.setting;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.databinding.DataBindingUtil;

import n7.ad2.R;
import n7.ad2.databinding.ActivitySettingBinding;
import n7.ad2.ui.setting.SettingsFragment;
import n7.ad2.utils.BaseActivity;

public class SettingActivity extends BaseActivity {

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_activity_setting, new SettingsFragment())
                    .commit();


        setToolbar();
    }

    private void setToolbar() {
        setSupportActionBar(binding.toolbarActivitySetting);
        binding.toolbarActivitySetting.setTitle(R.string.setting);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);//включаем кнопку домой
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//ставим значок стрелочки на кнопку
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
