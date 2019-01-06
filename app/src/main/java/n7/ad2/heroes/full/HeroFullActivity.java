package n7.ad2.heroes.full;

import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import n7.ad2.databinding.ActivityHeroFullBinding;
import n7.ad2.utils.AppExecutors;
import n7.ad2.R;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.Utils;

public class HeroFullActivity extends BaseActivity {

    public static final String HERO_NAME = "HERO_NAME";
    public static final String HERO_CODE_NAME = "HERO_CODE_NAME";
    private String codeName;
    private AppExecutors appExecutors;
    private ActivityHeroFullBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hero_full);


        if (savedInstanceState == null) {
            appExecutors = new AppExecutors();

            setToolbar();
            setViewPager();
            supportPostponeEnterTransition();
        } else {
            finish();
        }
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
        binding.vpActivityHeroFull.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        binding.vpActivityHeroFull.setOffscreenPageLimit(2);
        binding.tabActivityHeroFull.setupWithViewPager(binding.vpActivityHeroFull);
    }

    private void setToolbar() {
        try {
            String name = getIntent().getStringExtra(HERO_NAME);
            codeName = getIntent().getStringExtra(HERO_CODE_NAME);
            binding.toolbarActivityHeroFull.setTitle(name);
            setSupportActionBar(binding.toolbarActivityHeroFull);

            final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
            final int mActionBarSize = (int) styledAttributes.getDimension(0, 40) / 2;
            Bitmap icon = Utils.getBitmapFromAssets(this, String.format("heroes/%s/mini.webp", codeName));
            icon = Bitmap.createScaledBitmap(icon, mActionBarSize, mActionBarSize, false);
            final Drawable iconDrawable = new BitmapDrawable(getResources(), icon);
            binding.toolbarActivityHeroFull.setNavigationIcon(iconDrawable);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.toolbarActivityHeroFull.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utils.startAnimation(HeroFullActivity.this, binding.toolbarActivityHeroFull, "heroes/" + codeName + "/emoticon.webp", false, mActionBarSize);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
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
                    return HeroFragment.newInstance(codeName, appExecutors);
                case 1:
                    return HeroResponsesFragment.newInstance(codeName, appExecutors);
                case 2:
                    return GuideFragment.newInstance(codeName, appExecutors);
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
                case 0:
                    return getString(R.string.hero_info);
                case 1:
                    return getString(R.string.hero_sound);
                case 2:
                    return getString(R.string.hero_guide);
                default:
                    return "";
            }
        }
    }
}

