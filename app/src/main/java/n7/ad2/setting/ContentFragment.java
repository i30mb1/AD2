package n7.ad2.setting;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import n7.ad2.R;

public class ContentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private int idRes;

    public ContentFragment() {
    }

    public static ContentFragment newInstance(@DrawableRes int resId) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PARAM1, resId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idRes = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate_view, container, false);
        ImageView imageView = view.findViewById(R.id.iv);
        imageView.setImageResource(idRes);
        return view;
    }
}
