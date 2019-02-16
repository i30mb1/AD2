package n7.ad2.heroes.full;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewTreeObserver;

import n7.ad2.R;
import n7.ad2.databinding.ActivityHeroFullBinding;
import n7.ad2.utils.AppExecutors;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.Utils;

import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;

public class HeroFullActivity extends BaseActivity {

    public static final String HERO_NAME = "HERO_NAME";
    public static final String HERO_CODE_NAME = "HERO_CODE_NAME";
    private String heroCode;
    private ActivityHeroFullBinding binding;
    private String heroName;

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

        HeroFulViewModel viewModel = ViewModelProviders.of(this, new HeroFullViewModelFactory(getApplication(), heroCode, heroName)).get(HeroFulViewModel.class);

        setToolbar();
        setViewPager();
        supportPostponeEnterTransition();
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
        binding.tabActivityHeroFull.setupWithViewPager(binding.vpActivityHeroFull);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setToolbar() {
        try {
            sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "hero_" + heroCode + "_loaded"));
            binding.toolbarActivityHeroFull.setTitle(heroName);
            setSupportActionBar(binding.toolbarActivityHeroFull);

            final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
            final int mActionBarSize = (int) styledAttributes.getDimension(0, 40) / 2;


            try {
                Utils.startAnimation(HeroFullActivity.this, binding.toolbarActivityHeroFull, "heroes/" + heroCode + "/emoticon.webp", false, mActionBarSize);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.toolbarActivityHeroFull.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap icon = Utils.getBitmapFromAssets(HeroFullActivity.this, String.format("heroes/%s/mini.webp", heroCode));
                        icon = Bitmap.createScaledBitmap(icon, mActionBarSize, mActionBarSize, false);
                        final Drawable iconDrawable = new BitmapDrawable(getResources(), icon);
                        binding.toolbarActivityHeroFull.setNavigationIcon(iconDrawable);
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

