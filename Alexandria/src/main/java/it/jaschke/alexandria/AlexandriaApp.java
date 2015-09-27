package it.jaschke.alexandria;

import android.app.Application;
import android.content.res.Resources;

public class AlexandriaApp extends Application {

    private static AlexandriaApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Resources getRes() {
        return instance.getResources();
    }
}
