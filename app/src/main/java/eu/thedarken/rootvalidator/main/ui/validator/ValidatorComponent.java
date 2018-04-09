package eu.thedarken.rootvalidator.main.ui.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import dagger.Subcomponent;
import eu.darken.mvpbakery.injection.PresenterComponent;
import eu.darken.mvpbakery.injection.fragment.FragmentComponent;


@ValidatorComponent.Scope
@Subcomponent()
public interface ValidatorComponent extends PresenterComponent<ValidatorPresenter.View, ValidatorPresenter>, FragmentComponent<ValidatorFragment> {
    @Subcomponent.Builder
    abstract class Builder extends FragmentComponent.Builder<ValidatorFragment, ValidatorComponent> {

    }

    @Documented
    @javax.inject.Scope
    @Retention(RetentionPolicy.RUNTIME)
    @interface Scope {
    }
}
