package eu.thedarken.rootvalidator.tools;


import com.bugsnag.android.BeforeNotify;

import javax.inject.Inject;

import eu.thedarken.rootvalidator.AppComponent;
import eu.thedarken.rootvalidator.BuildConfig;


@AppComponent.Scope
public class BugsnagErrorHandler implements BeforeNotify {
    private final BugsnagTree bugsnagTree;

    @Inject
    public BugsnagErrorHandler(BugsnagTree bugsnagTree) {
        this.bugsnagTree = bugsnagTree;
    }

    @Override
    public boolean run(com.bugsnag.android.Error error) {
        bugsnagTree.update(error);

        return !BuildConfig.DEBUG;
    }
}
