package model.dyno;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class ApplicationContextProvider extends Application {
    private static Context sContext;

    private RefWatcher refWatcher;

    public void onCreate(){
        super.onCreate();

        sContext = getApplicationContext();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        enabledStrictMode();
        refWatcher = LeakCanary.install(this);

    }

    public static Context getsContext(){
        return sContext;
    }


    private static void enabledStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }


    public RefWatcher getRefWatcher() {
        return refWatcher;
    }
}
