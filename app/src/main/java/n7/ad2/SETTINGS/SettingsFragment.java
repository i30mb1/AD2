package n7.ad2.SETTINGS;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import n7.ad2.BuildConfig;
import n7.ad2.R;
import n7.ad2.activity.LicensesActivity;
import n7.ad2.activity.LogInActivity;
import n7.ad2.activity.MainActivity;

import static android.app.Activity.RESULT_OK;
import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.activity.BaseActivity.THEME_DARK;
import static n7.ad2.activity.BaseActivity.THEME_GRAY;
import static n7.ad2.activity.BaseActivity.THEME_WHITE;

public class SettingsFragment extends PreferenceFragment {

    private static Map<Preference, PreferenceGroup> buildPreferenceParentTree(final SettingsFragment activity) {
        final Map<Preference, PreferenceGroup> result = new HashMap<>();
        final Stack<PreferenceGroup> curParents = new Stack<>();
        curParents.add(activity.getPreferenceScreen());
        while (!curParents.isEmpty()) {
            final PreferenceGroup parent = curParents.pop();
            final int childCount = parent.getPreferenceCount();
            for (int i = 0; i < childCount; ++i) {
                final Preference child = parent.getPreference(i);
                result.put(child, parent);
                if (child instanceof PreferenceGroup)
                    curParents.push((PreferenceGroup) child);
            }
        }
        return result;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference theme = findPreference(getString(R.string.setting_theme_key));
        theme.setSummary(getPreferenceScreen().getSharedPreferences().getString(theme.getKey(), ""));
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

        Preference show_log = findPreference(getString(R.string.setting_show_log_key));
        show_log.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                recreateActivity();
                return true;
            }
        });

        Preference show_preview_image = findPreference(getString(R.string.setting_news_with_image_key));
        show_preview_image.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                recreateActivity();
                return true;
            }
        });


        Preference aboutApp = findPreference(getString(R.string.setting_about_key));
        aboutApp.setSummary(getString(R.string.setting_about_desc, BuildConfig.VERSION_NAME));
        aboutApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), LicensesActivity.class));
                return true;
            }
        });
        Preference donate = findPreference(getString(R.string.setting_donate_key));
        donate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((SettingActivity) getActivity()).initDialogDonate();
                return true;
            }
        });

//        if (getPreferenceManager().getSharedPreferences().getBoolean(PREMIUM, false)) {
//            donate.setSummary(R.string.all_activated);
//        } else {
//            donate.setSummary(R.string.all_not_activated);
//        }

        Preference contactUs = findPreference(getString(R.string.setting_contact_us_key));
        contactUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                StringBuilder sendInfo = new StringBuilder();
                sendInfo.append("\n\n\n--------------------------\n");
//                sendInfo.append(getString(R.string.all_write_here));
                sendInfo.append("App Version: ").append(getString(R.string.setting_about_desc, BuildConfig.VERSION_NAME)).append("\n");
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
                intent = Intent.createChooser(intent, getString(R.string.setting_tell_friend));
                startActivity(intent);
                return true;
            }
        });

        //удалить пункт из меню
        if (!getPreferenceManager().getSharedPreferences().getBoolean(PREMIUM, false)) {
            final Map<Preference, PreferenceGroup> preferenceParentTree = buildPreferenceParentTree(SettingsFragment.this);
            final PreferenceGroup preferenceGroup = preferenceParentTree.get(contactUs);
            if (preferenceGroup != null) preferenceGroup.removePreference(contactUs);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void createDialogTheme(final Preference preference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_theme);
        AlertDialog dialogTheme = builder.show();

        dialogTheme.findViewById(R.id.b_dialog_theme_dark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preference.getEditor().putString(preference.getKey(), THEME_DARK).apply();
                recreateActivity();
            }
        });
        dialogTheme.findViewById(R.id.b_dialog_theme_gray).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preference.getEditor().putString(preference.getKey(), THEME_GRAY).apply();
                recreateActivity();
            }
        });
        dialogTheme.findViewById(R.id.b_dialog_theme_white).setOnClickListener(new View.OnClickListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.dialog_info);
                final AlertDialog dialog = builder.show();
                TextView textView = dialog.findViewById(R.id.tv_dialog_info);
                textView.setText(R.string.setting_help_log_in_text);
                Button button = dialog.findViewById(R.id.b_dialog_info);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPreferenceScreen().getSharedPreferences().edit().putBoolean(getString(R.string.setting_help_log_in_key), false).apply();
                        dialog.dismiss();
                    }
                });
            }
        }
    }

}
