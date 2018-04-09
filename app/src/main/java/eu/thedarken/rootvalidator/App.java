package eu.thedarken.rootvalidator;

import android.app.Activity;
import android.app.Application;

import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Client;

import javax.inject.Inject;

import eu.darken.mvpbakery.injection.ComponentSource;
import eu.darken.mvpbakery.injection.ManualInjector;
import eu.darken.mvpbakery.injection.activity.HasManualActivityInjector;
import eu.thedarken.rootvalidator.tools.BugsnagErrorHandler;
import eu.thedarken.rootvalidator.tools.BugsnagTree;
import timber.log.Timber;

public class App extends Application implements HasManualActivityInjector {

    @Inject BugsnagTree bugsnagTree;
    @Inject BugsnagErrorHandler errorHandler;
    @Inject AppComponent appComponent;
    @Inject ComponentSource<Activity> activityInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        DaggerAppComponent.builder()
                .androidModule(new AndroidModule(this))
                .build()
                .injectMembers(this);

        Timber.plant(bugsnagTree);
        Client bugsnagClient = Bugsnag.init(this);
        bugsnagClient.beforeNotify(errorHandler);

        Timber.d("Bugsnag setup done!");
    }

    @Override
    public ManualInjector<Activity> activityInjector() {
        return activityInjector;
    }
}