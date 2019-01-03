package n7.ad2;

import android.databinding.BindingAdapter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class CustomsDataBinding {

    @BindingAdapter("app:url")
    public static void loadUrl(ImageView view, String url) {
        Picasso.get()
                .load("file:///android_asset/heroes/" + url + "/full.webp")
                .error(R.drawable.hero_placeholder_error)
                .placeholder(R.drawable.hero_placeholder)
                .into(view);
    }

    @BindingAdapter("app:url2")
    public static void loadRes(ImageView view, int resId) {
        Picasso.get().load(resId).into(view);
    }


    @BindingAdapter("isBusy")
    public static void setItBusy(View view, boolean isBusy) {
        Animation animation = view.getAnimation();
        if (isBusy && animation == null) {
            view.startAnimation(getAnimation());
        } else if (animation != null) {
            animation.cancel();
            view.setAnimation(null);
        }
    }

    private static Animation getAnimation() {
        RotateAnimation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        anim.setRepeatCount(TranslateAnimation.INFINITE);
        anim.setRepeatMode(TranslateAnimation.REVERSE);
        return anim;

    }
}
