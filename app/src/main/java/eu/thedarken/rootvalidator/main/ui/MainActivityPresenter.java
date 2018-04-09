package eu.thedarken.rootvalidator.main.ui;

import javax.inject.Inject;

import eu.darken.mvpbakery.base.Presenter;
import eu.darken.mvpbakery.injection.ComponentPresenter;

public class MainActivityPresenter extends ComponentPresenter<MainActivityPresenter.View, MainActivityComponent> {

    @Inject
    public MainActivityPresenter() {
    }

    interface View extends Presenter.View {
    }
}
