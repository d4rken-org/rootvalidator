package eu.thedarken.rootvalidator.main.ui;


import android.support.v4.app.Fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;
import eu.darken.mvpbakery.injection.PresenterComponent;
import eu.darken.mvpbakery.injection.activity.ActivityComponent;
import eu.thedarken.rootvalidator.main.ui.validator.ValidatorComponent;
import eu.thedarken.rootvalidator.main.ui.validator.ValidatorFragment;

@MainActivityComponent.Scope
@Subcomponent(modules = {
        MainActivityComponent.FragmentBinderModule.class
})
public interface MainActivityComponent extends ActivityComponent<MainActivity>, PresenterComponent<MainActivityPresenter.View, MainActivityPresenter> {

    @Subcomponent.Builder
    abstract class Builder extends ActivityComponent.Builder<MainActivity, MainActivityComponent> {

    }

    @javax.inject.Scope
    @Retention(RetentionPolicy.RUNTIME)
    @interface Scope {
    }

    @Module(subcomponents = {
            ValidatorComponent.class
    })
    abstract class FragmentBinderModule {

        @Binds
        @IntoMap
        @FragmentKey(ValidatorFragment.class)
        abstract Factory<? extends Fragment> volumes(ValidatorComponent.Builder impl);
    }
}
