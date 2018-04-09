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

public class SuperUserBinaryResult implements TestResult {
    private final List<SuperUserBinary> binaries;

    public SuperUserBinaryResult(Builder builder) {
        super();
        this.binaries = builder.binaries;
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.label_subinary_test);
    }

    public SuperUserBinary getPrimary() {
        for (SuperUserBinary suBinary : binaries) {
            if (suBinary.isPrimary()) {
                return suBinary;
            }
        }
        return null;
    }

    @Override
    public Outcome getOutcome() {
        if (binaries.isEmpty() || (binaries.size() == 1 && getPrimary() == null)) {
            return Outcome.NEGATIVE;
        } else if (binaries.size() > 1) {
            return Outcome.NEUTRAL;
        } else {
            return Outcome.POSITIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (binaries.isEmpty() || (binaries.size() == 1 && getPrimary() == null)) {
            return context.getString(R.string.label_no_subinary);
        } else {
            return context.getString(R.string.label_subinary_available);
        }
    }

    @Override
    public List<Criterion> getCriteria(Context context) {
        List<Criterion> criteria = new ArrayList<>();
        if (getPrimary() != null) {
            criteria.add(new Criterion(Outcome.POSITIVE, context.getString(R.string.msg_subinary_via_path)));
        } else {
            criteria.add(new Criterion(Outcome.NEGATIVE, context.getString(R.string.msg_subinary_not_via_path)));
        }
        for (SuperUserBinary binary : binaries) {
            Outcome type = Outcome.NEUTRAL;
            if (binary.isPrimary()) type = Outcome.POSITIVE;
            String output = binary.getPath() + "\n"
                    + binary.getPermission() + " " + binary.getOwner() + ":" + binary.getGroup() + "\n"
                    + binary.getRaw();
            criteria.add(new Criterion(type, output));
        }
        return criteria;
    }


    static class Builder {
        private final List<SuperUserBinary> binaries = new ArrayList<>();

        Builder binaries(List<SuperUserBinary> binaries) {
            this.binaries.addAll(binaries);
            return this;
        }

        public SuperUserBinaryResult build() {
            return new SuperUserBinaryResult(this);
        }
    }
}
