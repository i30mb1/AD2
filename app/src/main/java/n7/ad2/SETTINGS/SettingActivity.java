package n7.ad2.SETTINGS;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;

import n7.ad2.Commercial;
import n7.ad2.LocalImageHolderView;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.activity.BaseActivity;
import n7.ad2.purchaseUtils.IabBroadcastReceiver;
import n7.ad2.purchaseUtils.IabHelper;
import n7.ad2.purchaseUtils.IabResult;
import n7.ad2.purchaseUtils.Inventory;
import n7.ad2.purchaseUtils.Purchase;

import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.activity.MainActivity.OPEN_SUBSCRIPTION;

public class SettingActivity extends BaseActivity implements IabBroadcastReceiver.IabBroadcastListener {
    //    adb shell pm clear com.android.vending
//    private static final String ANNUAL_SUBSCRIPTION = "android.test.purchased";
//    public static final String ANNUAL_SUBSCRIPTION = "annual_subscription";
//    public static final String MONTHLY_SUBSCRIPTION = "monthly_subscription";
    public static final String ONCE_PER_MONTH_SUBSCRIPTION = "once_per_month_subscription";
    public static final String SAW_MY_REQUEST_OF_DONATION = "SAW_MY_REQUEST_OF_DONATION";

    private IabHelper mHelper;
    private BroadcastReceiver broadcastReceiver;
    private View dialog_donate;
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
    private ArrayList<Commercial> localImages = new ArrayList<>();

    private void checkInventory() {
        try {
            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    if (mHelper == null && result.isFailure()) return;
                    if (inv.hasPurchase(ONCE_PER_MONTH_SUBSCRIPTION)) {
                        MySharedPreferences.getSharedPreferences(SettingActivity.this).edit().putBoolean(PREMIUM, true).apply();
                        if (dialog_donate != null) {
                            TextView textView = dialog_donate.findViewById(R.id.tv_dialog_donate_is_activated);
                            if (textView != null) {
                                textView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                                textView.setText(R.string.all_activated);
                            }
                        }
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

    public void initDialogDonate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog_donate = getLayoutInflater().inflate(R.layout.dialog_donate, null);
        builder.setView(dialog_donate);
        final AlertDialog dialog = builder.create();

        ConvenientBanner convenientBanner = dialog_donate.findViewById(R.id.convenientBanner);
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Holder createHolder(View itemView) {
                return new LocalImageHolderView(itemView);
            }

            @Override
            public int getLayoutId() {
                return R.layout.item_list_commercial;
            }
        }, localImages)
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);

        Button b_dialog_donate_month = dialog_donate.findViewById(R.id.b_dialog_donate_month);
        if (MySharedPreferences.getSharedPreferences(SettingActivity.this).getBoolean(PREMIUM, false)) {
            b_dialog_donate_month.setText(R.string.all_deactivate);
        }
        TextView textView = dialog_donate.findViewById(R.id.tv_dialog_donate_is_activated);
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
        firebaseAnalytics.logEvent(SAW_MY_REQUEST_OF_DONATION,null);
        dialog.show();
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
        setContentView(R.layout.activity_setting);


        initImagesForCommercial();
        initPurchaseHelper();
        setToolbar();
        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
        checkIfNeedShowSubscription();
    }

    private void checkIfNeedShowSubscription() {
        if (getIntent().getBooleanExtra(OPEN_SUBSCRIPTION, false)) {
            initDialogDonate();
        }
    }

    private void initImagesForCommercial() {
        Commercial commercial_1 = new Commercial();
        commercial_1.image = R.drawable.commercial_1;
        commercial_1.description = getString(R.string.commercial_1);
        localImages.add(commercial_1);
       Commercial commercial_2 = new Commercial();
        commercial_2.image = R.drawable.commercial_2;
        commercial_2.description = getString(R.string.commercial_2);
        localImages.add(commercial_2);
       Commercial commercial_3 = new Commercial();
        commercial_3.image = R.drawable.commercial_3;
        commercial_3.description = getString(R.string.commercial_3);
        localImages.add(commercial_3);
        Commercial commercial_4 = new Commercial();
        commercial_4.image = R.drawable.commercial_4;
        commercial_4.description = getString(R.string.commercial_4);
        localImages.add(commercial_4);
        Commercial commercial_5 = new Commercial();
        commercial_5.image = R.drawable.commercial_5;
        commercial_5.description = getString(R.string.commercial_5);
        localImages.add(commercial_5);
        Commercial commercial_6 = new Commercial();
        commercial_6.image = R.drawable.commercial_6;
        commercial_6.description = getString(R.string.commercial_6);
        localImages.add(commercial_6);
        Commercial commercial_7 = new Commercial();
        commercial_7.image = R.drawable.commercial_7;
        commercial_7.description = getString(R.string.commercial_7);
        localImages.add(commercial_7);
         Commercial commercial_8 = new Commercial();
        commercial_8.image = R.drawable.commercial_8;
        commercial_8.description = getString(R.string.commercial_8);
        localImages.add(commercial_8);
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.setting);
        setSupportActionBar(toolbar);
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
    public void receivedBroadcast() {
        checkInventory();
    }

}
