/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.R;
import eu.thedarken.rootvalidator.ui.ColorFramedCircleDrawable;

public abstract class TestInfo implements Parcelable {
    private final List<String> mRaw = new ArrayList<>();
    protected final String mTestTitle;

    protected TestInfo(String testTitle) {
        mTestTitle = testTitle;
    }

    public String getTitle() {
        return mTestTitle;
    }

    public abstract Result.Outcome getOutcome();

    public Drawable getIcon(Context context) {
        if (getOutcome() == Result.Outcome.POSITIVE) {
            return getPositiveD(context);
        } else if (getOutcome() == Result.Outcome.NEGATIVE) {
            return getNegativeD(context);
        } else {
            return getNeutralD(context);
        }
    }

    public static Drawable getPositiveD(Context context) {
        return new ColorFramedCircleDrawable(null, 48, context.getResources().getColor(R.color.positive));
    }

    public static Drawable getNeutralD(Context context) {
        return new ColorFramedCircleDrawable(null, 48, context.getResources().getColor(R.color.neutral));
    }

    public static Drawable getNegativeD(Context context) {
        return new ColorFramedCircleDrawable(null, 48, context.getResources().getColor(R.color.negative));
    }

    public String getPrimaryInfo(Context context) {
        return null;
    }

    public List<BP> getCriterias(Context context) {
        return null;
    }

    public List<String> getRaw() {
        return mRaw;
    }

    public List<String> getDetails(Context context) {
        List<String> details = new ArrayList<>();
        details.add("##### " + getTitle() + " #####");
        details.add("Result:");
        details.add("   " + getPrimaryInfo(context));
        details.add("Extras:");
        for (BP bp : getCriterias(context)) {
            details.add("   " + bp.getText());
        }
        return details;
    }
}
