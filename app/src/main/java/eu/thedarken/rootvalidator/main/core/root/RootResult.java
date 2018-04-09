/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.root;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import eu.darken.rxshell.root.Root;
import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.main.core.Criterion;
import eu.thedarken.rootvalidator.main.core.Outcome;
import eu.thedarken.rootvalidator.main.core.TestResult;

public class RootResult implements TestResult {
    private final Root.State state;
    private final boolean exitCodeIssue;
    private final boolean launchIssue;
    private final boolean idIssue;

    RootResult(Builder builder) {
        this.state = builder.state;
        this.exitCodeIssue = builder.exitCodeIssue;
        this.launchIssue = builder.launchIssue;
        this.idIssue = builder.idIssue;
    }

    @Override
    public String getLabel(Context context) {
        return context.getString(R.string.label_general_root_test);
    }

    @Override
    public Outcome getOutcome() {
        if (state == Root.State.ROOTED) {
            return Outcome.POSITIVE;
        } else if (state == Root.State.DENIED) {
            return Outcome.NEUTRAL;
        } else {
            return Outcome.NEGATIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (state == Root.State.ROOTED) {
            return context.getString(R.string.label_root_available);
        } else if (state == Root.State.DENIED) {
            return context.getString(R.string.label_root_denied);
        } else {
            return context.getString(R.string.label_root_unavailable);
        }
    }

    @Override
    public List<Criterion> getCriteria(Context context) {
        List<Criterion> criteria = new ArrayList<>();
        if (launchIssue) {
            criteria.add(new Criterion(Outcome.NEGATIVE, context.getString(R.string.msg_failed_to_launch_shell)));
        } else {
            criteria.add(new Criterion(Outcome.POSITIVE, context.getString(R.string.msg_root_shell_launched)));
        }
        if (!launchIssue) {
            if (idIssue) {
                criteria.add(new Criterion(Outcome.NEGATIVE, context.getString(R.string.msg_id_unexpected)));
            } else {
                criteria.add(new Criterion(Outcome.POSITIVE, context.getString(R.string.msg_id_expected)));
            }
            if (exitCodeIssue) {
                criteria.add(new Criterion(Outcome.NEUTRAL, context.getString(R.string.msg_bad_exitcodes)));
            } else {
                criteria.add(new Criterion(Outcome.POSITIVE, context.getString(R.string.msg_good_exitcodes)));
            }
        }
        return criteria;
    }

    static class Builder {
        private boolean exitCodeIssue = false;
        private Root.State state = Root.State.UNAVAILABLE;
        private boolean launchIssue = false;
        private boolean idIssue = false;


        Builder state(Root.State state) {
            this.state = state;
            return this;
        }

        Builder launchIssue(boolean launchIssue) {
            this.launchIssue = launchIssue;
            return this;
        }

        Builder idIssue(boolean idIssue) {
            this.idIssue = idIssue;
            return this;
        }

        Builder exitCodeIssue(boolean exitCodeIssue) {
            this.exitCodeIssue = exitCodeIssue;
            return this;
        }

        RootResult build() {
            return new RootResult(this);
        }
    }
}
