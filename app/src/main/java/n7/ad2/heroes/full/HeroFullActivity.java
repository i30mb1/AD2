package n7.ad2.heroes.full;

import android.Manifest;
import android.annotation.SuppressLint;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.TypedArray;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewTreeObserver;

import java.io.File;

import n7.ad2.R;
import n7.ad2.databinding.ActivityHeroFullBinding;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.SnackbarUtils;
import n7.ad2.utils.Utils;

import static n7.ad2.heroes.full.HeroFulViewModel.FILE_EXIST;
import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;

public class HeroFullActivity extends BaseActivity {

    public static final String HERO_NAME = "HERO_NAME";
    public static final String HERO_CODE_NAME = "HERO_CODE_NAME";
    public static final String CURRENT_ITEM = "CURRENT_ITEM";
    public static int REQUESTED_PERMISSION = 1;
    private String heroCode;
    private ActivityHeroFullBinding binding;
    private String heroName;
    private HeroFulViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hero_full);

        if (savedInstanceState == null) {
            heroName = getIntent().getStringExtra(HERO_NAME);
            heroCode = getIntent().getStringExtra(HERO_CODE_NAME);
        } else {
            heroName = savedInstanceState.getString(HERO_NAME);
            heroCode = savedInstanceState.getString(HERO_CODE_NAME);
        }

        viewModel = ViewModelProviders.of(this, new HeroFullViewModelFactory(getApplication(), heroCode, heroName)).get(HeroFulViewModel.class);

        setToolbar();
        setViewPager();
        supportPostponeEnterTransition();
        setObservers();
    }

    private void setObservers() {
        viewModel.showSnackBar.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@StringRes Integer integer) {
                if (integer == FILE_EXIST) {
                    Snackbar.make(binding.getRoot(), R.string.hero_responses_sound_already_downloaded, Snackbar.LENGTH_LONG).setAction(R.string.open_file, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri selectedUri = Uri.parse(view.getContext().getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator);
                            Intent intentOpenFile = new Intent(Intent.ACTION_VIEW);
                            intentOpenFile.setDataAndType(selectedUri, "application/*");
                            if (intentOpenFile.resolveActivityInfo(view.getContext().getPackageManager(), 0) != null) {
                                view.getContext().startActivity(Intent.createChooser(intentOpenFile, view.getContext().getString(R.string.hero_responses_open_folder_with)));
                            } else {
                                // if you reach this place, it means there is no any file
                                // explorer app installed on your device
                            }
                        }
                    }).show();
                } else {
                    SnackbarUtils.showSnackbar(binding.getRoot(), getString(integer));
                }
            }
        });
        viewModel.grandPermission.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                ActivityCompat.requestPermissions(HeroFullActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTED_PERMISSION);
            }
        });
        viewModel.grandSetting.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@StringRes Integer redId) {
                SnackbarUtils.showSnackbarWithAction(binding.getRoot(), getString(redId), getString(R.string.all_enable), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        @SuppressLint("InlinedApi") Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(HERO_NAME, heroName);
        outState.putString(HERO_CODE_NAME, heroCode);
    }

    public void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                });
    }

    private void setViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        binding.vpActivityHeroFull.setAdapter(viewPagerAdapter);
        binding.vpActivityHeroFull.setOffscreenPageLimit(2);
        binding.vpActivityHeroFull.setCurrentItem(getPreferences(MODE_PRIVATE).getInt(CURRENT_ITEM, 0));
        binding.tabActivityHeroFull.setupWithViewPager(binding.vpActivityHeroFull);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferences(MODE_PRIVATE).edit().putInt(CURRENT_ITEM, binding.vpActivityHeroFull.getCurrentItem()).apply();
    }

    private void setToolbar() {
        try {
            sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "hero_" + heroCode + "_loaded"));
            binding.toolbarActivityHeroFull.setTitle(heroName);
            setSupportActionBar(binding.toolbarActivityHeroFull);

            final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
            final int mActionBarSize = (int) styledAttributes.getDimension(0, 40) / 2;


            try {
                Bitmap icon = Utils.getBitmapFromAssets(HeroFullActivity.this, String.format("heroes/%s/mini.webp", heroCode));
                icon = Bitmap.createScaledBitmap(icon, mActionBarSize, mActionBarSize, false);
                final Drawable iconDrawable = new BitmapDrawable(getResources(), icon);
                binding.toolbarActivityHeroFull.setNavigationIcon(iconDrawable);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.toolbarActivityHeroFull.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.startAnimation(HeroFullActivity.this, binding.toolbarActivityHeroFull, "heroes/" + heroCode + "/emoticon.webp", false, mActionBarSize);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HeroFragment.newInstance();
                case 1:
                    return ResponsesFragment.newInstance();
                case 2:
                    return GuideFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                case 0:
                    return getString(R.string.hero_info);
                case 1:
                    return getString(R.string.hero_sound);
                case 2:
                    return getString(R.string.hero_guide);
            }
        }
    }
}

