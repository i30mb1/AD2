package n7.ad2.utils;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;

import static n7.ad2.ui.streams.StreamsFullActivity.speed;

public class SmoothScrollableLinearLayoutManager extends LinearLayoutManager {
    private final Context context;

    public SmoothScrollableLinearLayoutManager(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(context) {
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return speed / displayMetrics.densityDpi;
            }
        };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    @Override
    public boolean isSmoothScrolling() {
        return super.isSmoothScrolling();
    }


}
