package eu.thedarken.rootvalidator;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;
import eu.thedarken.rootvalidator.main.ui.MainActivity;
import eu.thedarken.rootvalidator.main.ui.MainActivityComponent;

@Module(subcomponents = {
        MainActivityComponent.class
})
abstract class ActivityBinderModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> main(MainActivityComponent.Builder impl);

}