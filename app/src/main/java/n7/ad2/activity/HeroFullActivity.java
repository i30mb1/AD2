package n7.ad2.activity;

import android.content.Intent;
import android.content.res.TypedArray;
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

import n7.ad2.AppExecutors;
import n7.ad2.R;
import n7.ad2.fragment.GuideFragment;
import n7.ad2.fragment.HeroFragment;
import n7.ad2.fragment.HeroResponsesFragment;
import n7.ad2.utils.Utils;

public class HeroFullActivity extends BaseActivity {

    public static final String HERO_NAME = "HERO_NAME";
    public static final String HERO_CODE_NAME = "HERO_CODE_NAME";
    private Toolbar toolbar;
    private String heroFolder;
    private AppExecutors appExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hero_personal);

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
        ViewPager viewPager = findViewById(R.id.vp_activity_hero_personal);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        TabLayout tabLayout = findViewById(R.id.tab_activity_hero_personal);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setToolbar() {
        try {
            Intent intent = getIntent();
            toolbar = findViewById(R.id.toolbar);
            String heroName = intent.getStringExtra(HERO_NAME);
            toolbar.setTitle(heroName);
            setSupportActionBar(toolbar);
            heroFolder = intent.getStringExtra(HERO_CODE_NAME);

            final TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
            final int mActionBarSize = (int) styledAttributes.getDimension(0, 40) / 2;
            Bitmap icon = Utils.getBitmapFromAssets(this, String.format("heroes/%s/mini.webp", heroFolder));
            icon = Bitmap.createScaledBitmap(icon, mActionBarSize, mActionBarSize, false);
            final Drawable iconDrawable = new BitmapDrawable(getResources(), icon);
            toolbar.setNavigationIcon(iconDrawable);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utils.startAnimation(HeroFullActivity.this, toolbar, "heroes/" + heroFolder + "/emoticon.webp", true, mActionBarSize);
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
                    return HeroFragment.newInstance(heroFolder, appExecutors);
                case 1:
                    return HeroResponsesFragment.newInstance(heroFolder, appExecutors);
                case 2:
                    return GuideFragment.newInstance(heroFolder, appExecutors);
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

