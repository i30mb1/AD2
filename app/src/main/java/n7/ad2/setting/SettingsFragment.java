package n7.ad2.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import n7.ad2.BuildConfig;
import n7.ad2.R;
import n7.ad2.activity.LicensesActivity;
import n7.ad2.activity.LogInActivity;
import n7.ad2.main.MainActivity;

import static android.app.Activity.RESULT_OK;
import static n7.ad2.activity.BaseActivity.THEME_DARK;
import static n7.ad2.activity.BaseActivity.THEME_GRAY;
import static n7.ad2.activity.BaseActivity.THEME_WHITE;

public class SettingsFragment extends PreferenceFragment {

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        Preference theme = findPreference(getString(R.string.setting_theme_key));
        theme.setSummary(getPreferenceScreen().getSharedPreferences().getString(theme.getKey(), THEME_GRAY));
        theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                createDialogTheme(preference);
                return true;
            }
        });

        Preference account = findPreference(getString(R.string.setting_account_key));
        account.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean needAuthentication = (boolean) newValue;
                if (needAuthentication) {
                    startActivityForResult(new Intent(getActivity(), LogInActivity.class), LogInActivity.REQUEST_CODE_LOG_IN);
                }
                return true;
            }
        });

        Preference log = findPreference(getString(R.string.setting_log_key));
        log.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                recreateActivity();
                return true;
            }
        });

        Preference news = findPreference(getString(R.string.setting_news_key));
        news.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                recreateActivity();
                return true;
            }
        });

        Preference aboutApp = findPreference(getString(R.string.setting_about_key));
        aboutApp.setSummary(getString(R.string.setting_about_summary, BuildConfig.VERSION_NAME));
        aboutApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), LicensesActivity.class));
                return true;
            }
        });

        Preference subscription = findPreference(getString(R.string.setting_subscription_key));
        subscription.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((SettingActivity) getActivity()).showDialogDonate();
                return true;
            }
        });

        Preference contactUs = findPreference(getString(R.string.setting_contact_key));
        contactUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //noinspection StringBufferReplaceableByString
                StringBuilder sendInfo = new StringBuilder();
                sendInfo.append("\n\n\n--------------------------\n");
                sendInfo.append("App Version: ").append(getString(R.string.setting_about_summary, BuildConfig.VERSION_NAME)).append("\n");
                sendInfo.append("Device Brand: ").append(Build.BRAND).append("\n");
                sendInfo.append("Device Model: ").append(Build.MODEL).append("\n");
                sendInfo.append("OS: Android ").append(VERSION.RELEASE).append("(").append(VERSION.CODENAME).append(") \n");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("mailto:fate.i30mb1@gmail.com?subject=Feedback about AD2 on Android&body=" + sendInfo.toString()));
                startActivity(intent);
                return true;
            }
        });

        Preference add_friend = findPreference(getString(R.string.setting_tell_friend_key));
        add_friend.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_tell_friend_message));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_tell_friend_message));
                intent = Intent.createChooser(intent, getString(R.string.setting_tell_friend_title));
                startActivity(intent);
                return true;
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void createDialogTheme(final Preference preference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_theme);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();
        dialog.findViewById(R.id.b_dialog_theme_dark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preference.getEditor().putString(preference.getKey(), THEME_DARK).apply();
                recreateActivity();
            }
        });
        dialog.findViewById(R.id.b_dialog_theme_gray).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preference.getEditor().putString(preference.getKey(), THEME_GRAY).apply();
                recreateActivity();
            }
        });
        dialog.findViewById(R.id.b_dialog_theme_white).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preference.getEditor().putString(preference.getKey(), THEME_WHITE).apply();
                recreateActivity();
            }
        });
    }

    private void recreateActivity() {
        TaskStackBuilder.create(getActivity())
                .addNextIntent(new Intent(getActivity(), MainActivity.class))
                .addNextIntent(getActivity().getIntent())
                .startActivities();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK & requestCode == LogInActivity.REQUEST_CODE_LOG_IN) {
            boolean showDialogHelpLogIn = getPreferenceScreen().getSharedPreferences().getBoolean(getString(R.string.setting_help_log_in_key), true);
            if (showDialogHelpLogIn) {
                createDialogTips();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void createDialogTips() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_info);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();
        TextView textView = dialog.findViewById(R.id.tv_dialog_info);
        textView.setText(R.string.setting_help_log_in_text);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getPreferenceScreen().getSharedPreferences().edit().putBoolean(getString(R.string.setting_help_log_in_key), false).apply();
            }
        });
    }

}
