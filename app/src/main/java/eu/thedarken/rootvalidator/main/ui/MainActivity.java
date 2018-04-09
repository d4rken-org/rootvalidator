/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import eu.darken.mvpbakery.base.MVPBakery;
import eu.darken.mvpbakery.base.ViewModelRetainer;
import eu.darken.mvpbakery.injection.ComponentSource;
import eu.darken.mvpbakery.injection.InjectedPresenter;
import eu.darken.mvpbakery.injection.ManualInjector;
import eu.darken.mvpbakery.injection.PresenterInjectionCallback;
import eu.darken.mvpbakery.injection.fragment.HasManualFragmentInjector;
import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.main.ui.validator.ValidatorFragment;

public class MainActivity extends AppCompatActivity implements MainActivityPresenter.View, HasManualFragmentInjector {

    @Inject ComponentSource<Fragment> componentSource;

    public String getFragmentClass() {
        return ValidatorFragment.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MVPBakery.<MainActivityPresenter.View, MainActivityPresenter>builder()
                .presenterFactory(new InjectedPresenter<>(this))
                .presenterRetainer(new ViewModelRetainer<>(this))
                .addPresenterCallback(new PresenterInjectionCallback<>(this))
                .attach(this);

        setContentView(R.layout.activity_mainactivity_layout);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getFragmentClass());
        if (fragment == null) {
            fragment = Fragment.instantiate(this, getFragmentClass());
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, getFragmentClass()).commit();
        }
    }

    @Override
    public ManualInjector<Fragment> supportFragmentInjector() {
        return componentSource;
    }
}