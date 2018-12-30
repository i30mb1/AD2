package n7.ad2.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class UnscrollableLinearLayoutManager extends LinearLayoutManager {

    public UnscrollableLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

}
