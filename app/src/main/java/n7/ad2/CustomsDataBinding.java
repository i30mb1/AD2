package n7.ad2;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class CustomsDataBinding {

    @BindingAdapter({"app:url"})
    public static void loadImage(ImageView view, int resId) {
        Picasso.get().load(resId).into(view);
    }

}
