package eu.thedarken.rootvalidator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;


@Module
class AndroidModule {
    private final App app;

    AndroidModule(App app) {this.app = app;}

    @Provides
    @AppComponent.Scope
    Context context() {
        return app.getApplicationContext();
    }

    @Provides
    @AppComponent.Scope
    SharedPreferences defaultPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
