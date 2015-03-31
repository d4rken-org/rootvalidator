package eu.thedarken.rootvalidator.tests;

import android.content.Context;

/**
 * Created by darken on 01.03.2015.
 */
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
