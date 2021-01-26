package n7.ad2.ui.streams.utilsTwitch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;

public class TappableSurfaceView extends SurfaceView {
    private final ArrayList<TapListener> listeners = new ArrayList<>();
    private final GestureDetector.SimpleOnGestureListener gestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    for (TapListener l : listeners) {
                        l.onTap(e);
                    }

                    return (true);
                }
            };

    public TappableSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            gestureListener.onSingleTapUp(event);
        }

        return (true);
    }


    public void addTapListener(TapListener l) {
        listeners.add(l);
    }

    public void removeTapListener(TapListener l) {
        listeners.remove(l);
    }

    public interface TapListener {
        void onTap(MotionEvent event);
    }
}
