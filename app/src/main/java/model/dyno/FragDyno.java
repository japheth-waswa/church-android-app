package model.dyno;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.japhethwaswa.church.R;

public class FragDyno {

    private static Context context = ApplicationContextProvider.getsContext();
    private static Resources res = context.getResources();

    public static int getPrevPosition(String itemStringResName) {
        String preferenceFileKey = res.getString(R.string.preference_file_key);
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE);
        return sharedPref.getInt(itemStringResName, -1);
    }

    public static void saveToPreference(String itemStringResName,int position) {
        String preferenceFileKey = res.getString(R.string.preference_file_key);
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(itemStringResName, position);
        editor.commit();
    }

    public static void clearDataPreference(String[] itemStringResNames) {
        String preferenceFileKey = res.getString(R.string.preference_file_key);
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceFileKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        for(String itemStringResName : itemStringResNames){
            editor.putInt(itemStringResName, -1);
        }

        editor.commit();
    }
}
