package n7.ad2;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class CustomsDataBinding {

    @BindingAdapter({"app:url"})
    public static void loadImage(ImageView view, int resId) {
        Picasso.get().load(resId).into(view);
    }

}
