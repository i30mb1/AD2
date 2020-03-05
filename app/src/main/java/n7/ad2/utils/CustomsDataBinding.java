package n7.ad2.utils;

import android.content.res.TypedArray;
import androidx.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import androidx.transition.Slide;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import n7.ad2.R;

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

    @BindingAdapter("setMyBackground")
    public static void setBackground(View view, boolean check) {
        if (check) {
            TypedValue typedValue = new TypedValue();

            TypedArray a = view.getContext().obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
            int color = a.getColor(0, 0);
            view.setBackgroundColor(color);
            a.recycle();
        } else {
            TypedValue typedValue = new TypedValue();

            TypedArray a = view.getContext().obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.textColorSecondary});
            int color = a.getColor(0, 0);
            view.setBackgroundColor(color);
            a.recycle();
        }
    }
    @BindingAdapter("setMyTextColor")
    public static void setBackgroundText(TextView view, boolean check) {
        if (check) {
            TypedValue typedValue = new TypedValue();

            TypedArray a = view.getContext().obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
            int color = a.getColor(0, 0);
            view.setTextColor(color);
            a.recycle();
        } else {
            TypedValue typedValue = new TypedValue();

            TypedArray a = view.getContext().obtainStyledAttributes(typedValue.data, new int[] { android.R.attr.textColorSecondary});
            int color = a.getColor(0, 0);
            view.setTextColor(color);
            a.recycle();
        }
    }

    @BindingAdapter("vvv")
    public static void animateVisibility(View textView, boolean visibility) {
        if (textView != null) {
            TransitionManager.beginDelayedTransition((ViewGroup) textView.getRootView(),new Slide(Gravity.START).setDuration(1000));
            if (visibility) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @BindingAdapter("isBusy")
    public static void setItBusy(View view, boolean isBusy) {
        Animation animation = view.getAnimation();
        if (isBusy && animation == null) {
            view.setEnabled(false);
            view.startAnimation(getAnimation());
        } else if (animation != null) {
            view.setEnabled(true);
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
