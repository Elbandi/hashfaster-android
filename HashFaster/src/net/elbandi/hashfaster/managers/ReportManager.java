package net.elbandi.hashfaster.managers;

import net.elbandi.hashfaster.network.BuildConfig;

import org.acra.ACRA;

import android.app.Application;

public class ReportManager {

    public static void init(Application app) {
        if (BuildConfig.DEBUG) return;
        ACRA.init(app);
    }

    public static void putCustomData(String key, String value) {
        if (BuildConfig.DEBUG) return;
        ACRA.getErrorReporter().putCustomData(key, value);
    }

    public static void handleException(Throwable e) {
        if (BuildConfig.DEBUG) return;
        ACRA.getErrorReporter().handleException(e);
    }
}
