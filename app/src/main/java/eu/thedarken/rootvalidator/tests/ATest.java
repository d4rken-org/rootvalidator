/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests;

import android.content.Context;

public abstract class ATest {
    private final Context mContext;

    public ATest(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public abstract TestInfo test();
}
