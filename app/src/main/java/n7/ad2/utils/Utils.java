package n7.ad2.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static String readJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            return null;
        }
        return json;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static Bitmap getBitmapFromAssets(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();
        Bitmap bitmap = null;
        try {
            InputStream inputStream = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Drawable getDrawableFromAssets(Context context, String filePath) {
        InputStream ims = null;
//        String.format("heroes/%s/%s", hero, file)
        try {
            ims = context.getAssets().open(filePath);
            return Drawable.createFromStream(ims, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void startAnimation(Context context, final Toolbar img, String name, Boolean isLooped, int mActionBarSize) {
        Bitmap bitmapFromAssets = getBitmapFromAssets(context, name);
        if (bitmapFromAssets != null) {
            // cut bitmaps to array of bitmaps
            Bitmap[] bitmaps;
            int FRAME_W = 32;
            int FRAME_H = 32;
            int NB_FRAMES = (bitmapFromAssets.getWidth() / FRAME_W);
            int FRAME_DURATION = 70; // in ms !
            int COUNT_X = (bitmapFromAssets.getWidth() / FRAME_W);
            int COUNT_Y = 1;

            bitmaps = new Bitmap[NB_FRAMES];
            int currentFrame = 0;

            for (int i = 0; i < COUNT_Y; i++) {
                for (int j = 0; j < COUNT_X; j++) {
                    bitmaps[currentFrame] = Bitmap.createBitmap(bitmapFromAssets, FRAME_W * j, FRAME_H * i, FRAME_W, FRAME_H);
                    // apply scale factor
                    bitmaps[currentFrame] = Bitmap.createScaledBitmap(bitmaps[currentFrame], mActionBarSize, mActionBarSize, true);
                    if (++currentFrame >= NB_FRAMES) {
                        break;
                    }
                }
            }
            // create animation programmatically
            final AnimationDrawable animation = new AnimationDrawable();
            animation.setOneShot(isLooped); // repeat animation

            for (int i = 0; i < NB_FRAMES; i++) {
                animation.addFrame(new BitmapDrawable(context.getResources(), bitmaps[i]), FRAME_DURATION);
            }
            // load animation on image
            img.setNavigationIcon(animation);
            // start animation on image
            img.post(new Runnable() {
                @Override
                public void run() {
                    animation.start();
                }
            });
        }
    }
}
