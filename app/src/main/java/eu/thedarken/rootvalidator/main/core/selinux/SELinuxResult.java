/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.selinux;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import eu.darken.rxshell.root.SELinux;
import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.main.core.Criterion;
import eu.thedarken.rootvalidator.main.core.Outcome;
import eu.thedarken.rootvalidator.main.core.TestResult;

public class SELinuxResult implements TestResult {
    private final SELinux.State state;

    SELinuxResult(Builder builder) {
        this.state = builder.state;
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.label_selinux_test);
    }

    public SELinux.State getState() {
        return state;
    }

    @Override
    public Outcome getOutcome() {
        if (getState() == SELinux.State.ENFORCING) {
            return Outcome.NEUTRAL;
        } else if (getState() == SELinux.State.DISABLED) {
            return Outcome.NEUTRAL;
        } else {
            return Outcome.POSITIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (getState() == SELinux.State.ENFORCING) {
            return context.getString(R.string.label_selinux_enforcing);
        } else if (getState() == SELinux.State.PERMISSIVE) {
            return context.getString(R.string.label_selinux_permissive);
        } else if (getState() == SELinux.State.DISABLED) {
            return context.getString(R.string.label_selinux_disabled);
        } else {
            return context.getString(R.string.label_selinux_unknown);
        }
    }

    @Override
    public List<Criterion> getCriteria(Context context) {
        return new ArrayList<>();
    }

    static class Builder {
        SELinux.State state = SELinux.State.ENFORCING;

        Builder state(SELinux.State state) {
            this.state = state;
            return this;
        }

        SELinuxResult build() {
            return new SELinuxResult(this);
        }
    }

}
