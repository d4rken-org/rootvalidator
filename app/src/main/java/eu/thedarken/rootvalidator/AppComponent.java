package eu.thedarken.rootvalidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import dagger.Component;
import dagger.MembersInjector;


@AppComponent.Scope
@Component(modules = {
        ActivityBinderModule.class,
        AndroidModule.class,
        AppModule.class
})
public interface AppComponent extends MembersInjector<App> {
    void inject(App app);

    @Component.Builder
    interface Builder {
        Builder androidModule(AndroidModule module);

        AppComponent build();
    }

    @Documented
    @javax.inject.Scope
    @Retention(RetentionPolicy.RUNTIME)
    @interface Scope {
    }
}
