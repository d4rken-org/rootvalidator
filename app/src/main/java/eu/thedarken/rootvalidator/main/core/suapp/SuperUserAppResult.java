/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.suapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.main.core.Criterion;
import eu.thedarken.rootvalidator.main.core.Outcome;
import eu.thedarken.rootvalidator.main.core.TestResult;

public class SuperUserAppResult implements TestResult {
    private final List<SuperUserApp> suApps;

    public SuperUserAppResult(Builder builder) {
        super();
        this.suApps = builder.suApps;
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.label_superapp_test);
    }

    @Override
    public Outcome getOutcome() {
        if (suApps.size() > 1) {
            return Outcome.NEUTRAL;
        } else if (suApps.size() == 1) {
            return Outcome.POSITIVE;
        } else {
            return Outcome.NEGATIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (suApps.size() > 1) {
            return context.getString(R.string.label_multiple_superapps);
        } else if (suApps.size() == 1) {
            return context.getString(R.string.label_superapp_found);
        } else {
            return context.getString(R.string.label_no_superapp);
        }
    }

    @Override
    public List<Criterion> getCriteria(Context context) {
        List<Criterion> criteria = new ArrayList<>();
        for (SuperUserApp suApp : suApps) {
            String output = suApp.getName() + "\n" +
                    suApp.getPackageName() + " @ " + suApp.getVersionName() + " (" + suApp.getVersionCode() + ")";
            criteria.add(new Criterion(suApp.isPrimary() ? Outcome.POSITIVE : Outcome.NEUTRAL, output));
        }
        return criteria;
    }

    static class Builder {

        private List<SuperUserApp> suApps = new ArrayList<>();

        Builder apps(List<SuperUserApp> suApps) {
            this.suApps.addAll(suApps);
            return this;
        }

        SuperUserAppResult build() {
            return new SuperUserAppResult(this);
        }
    }
}
