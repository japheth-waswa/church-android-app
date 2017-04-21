package model.dyno;

import android.app.Application;
import android.content.Context;

public class ApplicationContextProvider extends Application {
    private static Context sContext;

    public void onCreate(){
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getsContext(){
        return sContext;
    }
}
