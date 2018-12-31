package n7.ad2.setting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import n7.ad2.R;

public class CustomPageAdapter extends PagerAdapter {

    private Context context;
    private List<Integer> images;
    private List<String> descriptions;

    public CustomPageAdapter(Context context, List<Integer> images, List<String> descriptions) {
        this.context = context;
        this.images = images;
        this.descriptions = descriptions;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list_commercial, null);

        TextView textView = view.findViewById(R.id.tv);
        ImageView imageView = view.findViewById(R.id.iv);

        textView.setText(descriptions.get(position));
        imageView.setImageResource(images.get(position));

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

}
