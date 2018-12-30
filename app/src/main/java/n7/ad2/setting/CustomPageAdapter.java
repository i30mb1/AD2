package n7.ad2.setting;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class CustomPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public CustomPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        int index = position % fragments.size();
        return fragments.get(index);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
