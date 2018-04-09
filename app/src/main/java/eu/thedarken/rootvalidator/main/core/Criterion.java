/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core;

import android.content.Context;

public class Criterion implements Result {

    private final Outcome outcome;
    private final String description;

    public Criterion(Outcome type, String description) {
        this.outcome = type;
        this.description = description;
    }

    @Override
    public Outcome getOutcome() {
        return outcome;
    }

    @Override
    public String getPrimaryInfo(Context context) {
        return description;
    }
}
