package n7.ad2.twitch.utilsTwitch;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import static n7.ad2.twitch.StreamsFullActivity.speed;

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
