/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core;

import android.content.Context;

import java.util.List;

import io.reactivex.Single;

public abstract class TestSuite {
    private final Context context;

    public TestSuite(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract Single<List<TestResult>> test();
}
