package n7.ad2.utils;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class CustomsDataBinding {

    @BindingAdapter({"srcPath", "srcHolder", "srcError"})
    public static void loadUrl(ImageView view, String srcPath, Drawable srcHolder, Drawable srcError) {
        Picasso.get()
                .load(srcPath)
                .error(srcError)
                .placeholder(srcHolder)
                .into(view);
    }

    @BindingAdapter({"srcPath", "withImage"})
    public static void loadUrl(ImageView view, String srcPath, Boolean withImage) {
        if (withImage != null && withImage) {
            Picasso.get().load(srcPath).into(view);
        }
    }

    @BindingAdapter("scrollTo")
    public static void scrollTo(RecyclerView recyclerView, int position) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
        }
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
        anim.setDuration(800);
        anim.setRepeatCount(TranslateAnimation.INFINITE);
        anim.setRepeatMode(TranslateAnimation.REVERSE);
        return anim;

    }
}
