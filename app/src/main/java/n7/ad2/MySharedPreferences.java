package n7.ad2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public abstract class MySharedPreferences {

    public static final long ANIMATION_DURATION = 300L;

    public static final String GITHUB_LAST_APK_URL = "https://github.com/i30mb1/AD2/blob/master/app/release/app-release.apk?raw=true";
    public static final String GITHUB_UPDATE_URL = "https://github.com/i30mb1/AD2/blob/master/updates.json?raw=true";
    public static final String LAST_FRAGMENT_SELECTED_KEY = "last_fragment_selected_key";
    public static final String PREMIUM = "PREMIUM";
    public static final String DATE_END_PREMIUM = "DATE_END_PREMIUM";
    public static final String RESPONSE_COUNT_KEY = "RESPONSE_COUNT_KEY";
    public static final String DAY_FOR_UPDATE = "DAY_FOR_UPDATE";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_PLACE = "USER_PLACE";
    public static final String IS_DAY_FOR_UPDATE_KEY = "IS_DAY_FOR_UPDATE_KEY";
    public static final String IS_DAY_FOR_RATE = "IS_DAY_FOR_RATE";
    public static final String IS_DAY_FOR_DONATE = "IS_DAY_FOR_DONATE";
    public static final String IS_USED_5_DAYS_BONUS_FROM_GAME1 = "IS_USED_5_DAYS_BONUS_FROM_GAME1";

    public static final String TWITCH_STREAMS_TYPED = "TWITCH_STREAMS_TYPED";
    public static final String NEVER = "NEVER";
    public static final int FREE_COUNT = 10;
    public static SharedPreferences INSTANCE_SP;
    public static int LAST_FRAGMENT_SELECTED = -1;
    public static String TODAY_DATE = "";
    public static boolean SHOW_PREMIUM_SWITCH = false;

    public static SharedPreferences getSharedPreferences(final Context context) {
        if (INSTANCE_SP == null) {
            synchronized (MySharedPreferences.class) {
                if (INSTANCE_SP == null) {
                    INSTANCE_SP = PreferenceManager.getDefaultSharedPreferences(context);
                    LAST_FRAGMENT_SELECTED = INSTANCE_SP.getInt(LAST_FRAGMENT_SELECTED_KEY, -1);
                }
            }
        }
        return INSTANCE_SP;
    }

    public static String getTodayDate() {
        if (TODAY_DATE.equals("")) {
            TODAY_DATE = new SimpleDateFormat("DDD", Locale.US).format(Calendar.getInstance().getTime());
        }
        return TODAY_DATE;
    }

}
