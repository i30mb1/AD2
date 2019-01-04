package n7.ad2.setting;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.LinkedList;
import java.util.List;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.activity.BaseActivity;
import n7.ad2.databinding.ActivitySettingBinding;
import n7.ad2.databinding.DialogDonateBinding;
import n7.ad2.purchaseUtils.IabHelper;
import n7.ad2.purchaseUtils.IabResult;
import n7.ad2.purchaseUtils.Inventory;
import n7.ad2.purchaseUtils.Purchase;

import static n7.ad2.MySharedPreferences.PREMIUM;

public class SettingActivity extends BaseActivity {

    public static final String ONCE_PER_MONTH_SUBSCRIPTION = "once_per_month_subscription";

    public static final String FIREBASE_DIALOG_DONATE_SAW = "FIREBASE_DIALOG_DONATE_SAW";

    public static final String INTENT_SHOW_DIALOG_DONATE = "INTENT_SHOW_DIALOG_DONATE";

    public ObservableBoolean isPremium = new ObservableBoolean(false);
    private IabHelper mHelper;
    private ActivitySettingBinding binding;
    private List<Integer> images = new LinkedList<>();
    private List<String> descriptions = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(binding.container.getId(), new SettingsFragment()).commit();
        }

        setToolbar();
        initPurchaseHelper();
        loadItemsForDialogSubscription();
        checkIfNeedShowSubscription();
    }

    private void checkInventory() {
        try {
            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    if (mHelper == null && result.isFailure()) return;
                    if (inv.hasPurchase(ONCE_PER_MONTH_SUBSCRIPTION)) {
                        isPremium.set(true);
                        MySharedPreferences.getSharedPreferences(SettingActivity.this).edit().putBoolean(PREMIUM, true).apply();
                    } else {
                        isPremium.set(false);
                        MySharedPreferences.getSharedPreferences(SettingActivity.this).edit().putBoolean(PREMIUM, false).apply();
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
    public void showDialogDonate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DialogDonateBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_donate, null, false);
        builder.setView(binding.getRoot());
        binding.setView(this);

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

        binding.viewPager.setAdapter(new CustomPageAdapter(getApplicationContext(), images, descriptions));
        binding.tabLayout.setupWithViewPager(binding.viewPager, true);

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.logEvent(FIREBASE_DIALOG_DONATE_SAW, null);
    }

    public void launchPurchaseSubscription() {
        try {
            mHelper.launchSubscriptionPurchaseFlow(SettingActivity.this, ONCE_PER_MONTH_SUBSCRIPTION, 7, new IabHelper.OnIabPurchaseFinishedListener() {
                @Override
                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                    if (mHelper == null) return;
                    if (result.isFailure()) return;
                    checkInventory();
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    private void loadItemsForDialogSubscription() {
        images.add(R.drawable.commercial_1);
        images.add(R.drawable.commercial_2);
        images.add(R.drawable.commercial_3);
        images.add(R.drawable.commercial_4);
        images.add(R.drawable.commercial_5);
        images.add(R.drawable.commercial_6);
        images.add(R.drawable.commercial_7);
        images.add(R.drawable.commercial_8);

        descriptions.add(getString(R.string.commercial_1));
        descriptions.add(getString(R.string.commercial_2));
        descriptions.add(getString(R.string.commercial_3));
        descriptions.add(getString(R.string.commercial_4));
        descriptions.add(getString(R.string.commercial_5));
        descriptions.add(getString(R.string.commercial_6));
        descriptions.add(getString(R.string.commercial_7));
        descriptions.add(getString(R.string.commercial_8));
    }

    private void checkIfNeedShowSubscription() {
        if (getIntent().getBooleanExtra(INTENT_SHOW_DIALOG_DONATE, false)) {
            showDialogDonate();
        }
    }

    private void initPurchaseHelper() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyr808/wY0nXtrh7WEp7ZXqkdZTcwgIaEfKvqKtjkR0KJm/O0B6mLAOn2lNYr8j/mrxprxUk1eLHJtUH2NGzCJzs40AdG5XL/2X34wQpKfLgHj95yybkOPQH7jtHdG15+jwkmT4X80icUpKfoEbHOLzwzQyUn97IdR4X2pQpbI4v5Xy6oYvBLtvcAZoYXUoTlUua+8N8ZGLFqTNlWeBk7rToC7jtXKEMDWYucjoMs2uyrZ2x04+/KFwakDH12MMsNmT1Xuo5Vx6gwZ9QflT6cMd5y0xQlXlGoWeeFUIEIMfyV4s1BSPs3LiYSgNrYLLJ24pznoPtW26Ro4xRpOV4cyQIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    checkInventory();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
