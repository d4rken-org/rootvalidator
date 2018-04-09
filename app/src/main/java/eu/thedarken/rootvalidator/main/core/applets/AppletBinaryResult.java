/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.applets;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.main.core.Criterion;
import eu.thedarken.rootvalidator.main.core.Outcome;
import eu.thedarken.rootvalidator.main.core.TestResult;

public class AppletBinaryResult implements TestResult {
    private final List<AppletBinary> binaries;

    public AppletBinaryResult(Builder builder) {
        this.binaries = builder.binaries;
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.label_applet_source_test);
    }

    public List<AppletBinary> getBinaries() {
        return binaries;
    }

    public AppletBinary getPrimary() {
        for (AppletBinary bb : binaries) {
            if (bb.isPrimary()) return bb;
        }
        return null;
    }

    @Override
    public Outcome getOutcome() {
        if (getPrimary() == null) {
            return Outcome.NEGATIVE;
        } else {
            return Outcome.POSITIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (getBinaries().size() == 0) {
            return context.getString(R.string.label_no_applet_source);
        } else if (getBinaries().size() == 1) {
            return context.getString(R.string.label_applet_source_found);
        } else {
            return context.getString(R.string.label_multiple_applet_sources);
        }
    }

    @Override
    public List<Criterion> getCriteria(Context context) {
        List<Criterion> criteria = new ArrayList<>();
        AppletBinary primBB = getPrimary();
        if (primBB != null) {
            criteria.add(new Criterion(Outcome.POSITIVE, context.getString(R.string.msg_available_via_path)));
            criteria.add(new Criterion(Outcome.POSITIVE, primBB.getPath() + "\n" + primBB.getVersion()));
        } else {
            criteria.add(new Criterion(Outcome.NEGATIVE, context.getString(R.string.msg_not_available_via_path)));
        }
        return criteria;
    }

    public static class Builder {
        private final List<AppletBinary> binaries = new ArrayList<>();

        Builder binaries(AppletBinary binary) {
            this.binaries.add(binary);
            return this;
        }

        public AppletBinaryResult build() {
            return new AppletBinaryResult(this);
        }
    }
}
