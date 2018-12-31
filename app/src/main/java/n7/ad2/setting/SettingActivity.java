package n7.ad2.setting;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.activity.BaseActivity;
import n7.ad2.databinding.ActivitySettingBinding;
import n7.ad2.purchaseUtils.IabBroadcastReceiver;
import n7.ad2.purchaseUtils.IabHelper;
import n7.ad2.purchaseUtils.IabResult;
import n7.ad2.purchaseUtils.Inventory;
import n7.ad2.purchaseUtils.Purchase;

import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.activity.MainActivity.OPEN_SUBSCRIPTION;

public class SettingActivity extends BaseActivity implements IabBroadcastReceiver.IabBroadcastListener {

    public static final String ONCE_PER_MONTH_SUBSCRIPTION = "once_per_month_subscription";
    public static final String SAW_MY_REQUEST_OF_DONATION = "SAW_MY_REQUEST_OF_DONATION";

    private IabHelper mHelper;
    private final IabHelper.OnIabPurchaseFinishedListener finishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            if (mHelper == null) return;
            if (result.isFailure()) return;
            if (result.isSuccess() && info.getSku().equals(ONCE_PER_MONTH_SUBSCRIPTION)) {
                checkInventory();
            }

        }
    };
    private BroadcastReceiver broadcastReceiver;
    private ActivitySettingBinding binding;

    private void checkInventory() {
        try {
            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    if (mHelper == null && result.isFailure()) return;
                    if (inv.hasPurchase(ONCE_PER_MONTH_SUBSCRIPTION)) {
                        MySharedPreferences.getSharedPreferences(SettingActivity.this).edit().putBoolean(PREMIUM, true).apply();
//                        if (dialog_donate != null) {
//                            TextView textView = dialog_donate.findViewById(R.id.tv_is_activated);
//                            if (textView != null) {
//                                textView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
//                                textView.setText(R.string.all_activated);
//                            }
//                        }
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        Long inMemoryTime = MySharedPreferences.getSharedPreferences(SettingActivity.this).getLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis());
                        Long currentTime = calendar.getTimeInMillis();
                        if (inMemoryTime > currentTime) {
                            MySharedPreferences.getSharedPreferences(SettingActivity.this).edit().putBoolean(MySharedPreferences.PREMIUM, true).apply();
                        } else {
                            MySharedPreferences.getSharedPreferences(SettingActivity.this).edit().putBoolean(MySharedPreferences.PREMIUM, false).apply();
                        }
                    }
//            if (inv.hasPurchase(MONTHLY_SUBSCRIPTION)) {
//                try {
//                    mHelper.consumeAsync(inv.getPurchase(MONTHLY_SUBSCRIPTION), new IabHelper.OnConsumeFinishedListener() {
//                        @Override
//                        public void onConsumeFinished(Purchase purchase, IabResult result) {
//                            saveTimePremiumMonth();
//                        }
//                    });
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    e.printStackTrace();
//                }
//            }
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("ConstantConditions")
    public void showDialogSubscription() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_donate);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

        final ViewPager viewPager = dialog.findViewById(R.id.viewPager);
        TabLayout tabLayout = dialog.findViewById(R.id.indicator);

        final List<Integer> images = new LinkedList<>();
        images.add(R.drawable.commercial_1);
        images.add(R.drawable.commercial_2);
        images.add(R.drawable.commercial_3);

        List<String> descriptions = new LinkedList<>();
        descriptions.add(getString(R.string.commercial_1));
        descriptions.add(getString(R.string.commercial_2));
        descriptions.add(getString(R.string.commercial_3));

        viewPager.setAdapter(new CustomPageAdapter(getApplicationContext(), images, descriptions));
        tabLayout.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (viewPager.getCurrentItem() < images.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        },2000,2000);

        Button b_dialog_donate_month = dialog.findViewById(R.id.b_subscription);
        if (MySharedPreferences.getSharedPreferences(SettingActivity.this).getBoolean(PREMIUM, false)) {
            b_dialog_donate_month.setText(R.string.all_deactivate);
        }
        TextView textView = dialog.findViewById(R.id.tv_is_activated);
        if (MySharedPreferences.getSharedPreferences(SettingActivity.this).getBoolean(PREMIUM, false)) {
            textView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            textView.setText(R.string.all_not_activated);
        }
        b_dialog_donate_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchPurchase(ONCE_PER_MONTH_SUBSCRIPTION);
            }
        });

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.logEvent(SAW_MY_REQUEST_OF_DONATION, null);
    }

    private int loadTimePremium() {
        Calendar calendar = Calendar.getInstance();
        Long inMemoryTime = MySharedPreferences.getSharedPreferences(this).getLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis());
        if (inMemoryTime > calendar.getTimeInMillis()) {
            Long remainDays = inMemoryTime - calendar.getTimeInMillis();
            if (remainDays < 86400000L) return 1;
            int remainDaysInInt = (int) (remainDays / 86400000L);
            return remainDaysInInt;
        } else return 0;
    }


    private void launchPurchase(String SKU) {
        try {
            mHelper.launchSubscriptionPurchaseFlow(SettingActivity.this, SKU, 7, finishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(binding.container.getId(), new SettingsFragment()).commit();
        }

        setToolbar();
        initPurchaseHelper();

        checkIfNeedShowSubscription();
    }

    private void checkIfNeedShowSubscription() {
        if (getIntent().getBooleanExtra(OPEN_SUBSCRIPTION, false)) {
            showDialogSubscription();
        }
    }

    private void initPurchaseHelper() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyr808/wY0nXtrh7WEp7ZXqkdZTcwgIaEfKvqKtjkR0KJm/O0B6mLAOn2lNYr8j/mrxprxUk1eLHJtUH2NGzCJzs40AdG5XL/2X34wQpKfLgHj95yybkOPQH7jtHdG15+jwkmT4X80icUpKfoEbHOLzwzQyUn97IdR4X2pQpbI4v5Xy6oYvBLtvcAZoYXUoTlUua+8N8ZGLFqTNlWeBk7rToC7jtXKEMDWYucjoMs2uyrZ2x04+/KFwakDH12MMsNmT1Xuo5Vx6gwZ9QflT6cMd5y0xQlXlGoWeeFUIEIMfyV4s1BSPs3LiYSgNrYLLJ24pznoPtW26Ro4xRpOV4cyQIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    broadcastReceiver = new IabBroadcastReceiver(SettingActivity.this);
                    IntentFilter intentFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    registerReceiver(broadcastReceiver, intentFilter);
                    checkInventory();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    private void setToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(R.string.setting);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);//включаем кнопку домой
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//ставим значок стрелочки на кнопку
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                supportFinishAfterTransition();
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void receivedBroadcast() {
        checkInventory();
    }

}
