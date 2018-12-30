package n7.ad2.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;

public class MultiStreamsFragment extends Fragment {


    private LinearLayout ll_fragment_multi_streams;
    private View view;
    private final BroadcastReceiver connections = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ll_fragment_multi_streams != null) {
                ll_fragment_multi_streams.removeView(view.findViewById(intent.getIntExtra("id", 0)));
                addFragmentStream();
            }
        }
    };

    public MultiStreamsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_multi_streams, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void addFragmentStream() {
        if (getContext() != null) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setId(View.generateViewId());
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLayout.setLayoutParams(param);
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(frameLayout.getId(), new SingleStreamFragment()).commit();
            }
            ll_fragment_multi_streams.addView(frameLayout);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fragment_streams_plus:
                addFragmentStream();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        IntentFilter filter = new IntentFilter("refresh_ll");
        if (getContext() != null)
            getContext().registerReceiver(connections, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null)
            getContext().unregisterReceiver(connections);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_multi_streams, container, false);

//        showInstructionDialog();
        ll_fragment_multi_streams = view.findViewById(R.id.ll_fragment_multi_streams);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_fragment_multi_streams_1, new SingleStreamFragment()).commit();
        }

        return view;
    }


    private void showInstructionDialog() {
        if (getContext() != null && MySharedPreferences.getSharedPreferences(getContext()).getBoolean(getString(R.string.multi_streams_fragment_tip_key), true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(R.layout.dialog_info);
            final AlertDialog dialog = builder.show();
            TextView tv_dialog_tip = dialog.findViewById(R.id.tv_dialog_info);
            if (tv_dialog_tip != null)
                tv_dialog_tip.setText(R.string.multi_streams_fragment_tip);
            tv_dialog_tip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MySharedPreferences.getSharedPreferences(getContext()).edit().putBoolean(getString(R.string.multi_streams_fragment_tip_key), false).apply();
                        dialog.dismiss();
                    }
                });
        }
    }

}
