package techhunt.developers.library;

import android.app.Application;
import android.content.Context;

import com.orhanobut.hawk.Hawk;

public class App extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Hawk.init(this).build();
    }

    public static Context getContext() {
        return context;
    }
}