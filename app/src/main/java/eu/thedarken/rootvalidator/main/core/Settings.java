package eu.thedarken.rootvalidator.main.core;

import android.content.SharedPreferences;

import javax.inject.Inject;

import eu.thedarken.rootvalidator.AppComponent;

@AppComponent.Scope
public class Settings {
    private static final String KEY_LAST_NAG_TIME = "core.upgrade.lastnag";
    private final SharedPreferences preferences;

    @Inject
    public Settings(SharedPreferences preferences) {this.preferences = preferences;}

    public long getLastUpgradeNagTime() {
        return preferences.getLong(KEY_LAST_NAG_TIME, 0);
    }

    public void setLastUpgradeNagTime(long time) {
        preferences.edit().putLong(KEY_LAST_NAG_TIME, time).apply();
    }
}
