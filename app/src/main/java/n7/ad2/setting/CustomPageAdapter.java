package n7.ad2.setting;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
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

    CustomPageAdapter(Context context, List<Integer> images, List<String> descriptions) {
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

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }


    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list_commercial, container,false);

        TextView textView = view.findViewById(R.id.tv_item_list_commercial);
        ImageView imageView = view.findViewById(R.id.iv_item_list_commercial);

        textView.setText(descriptions.get(position));
        imageView.setImageResource(images.get(position));

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }
}
