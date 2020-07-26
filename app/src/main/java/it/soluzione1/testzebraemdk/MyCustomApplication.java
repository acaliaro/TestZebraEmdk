package it.soluzione1.testzebraemdk;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.soluzione1.testzebraemdk.utility.Utility;

public class MyCustomApplication extends Application {

    public static MyCustomApplication instance;
    private static Context context;

    int duration = 20;
    static final Logger _logger = LoggerFactory.getLogger(MyCustomApplication.class);

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();


        Utility.configureLogbackDirectly();

    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        _logger.error("onLowMemory");
    }

    public static MyCustomApplication getInstance() {
        return instance;
    }
    public static Context getAppContext() {
        return context;
    }
}
