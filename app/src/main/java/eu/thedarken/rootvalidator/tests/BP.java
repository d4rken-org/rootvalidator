/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests;

import android.graphics.drawable.Drawable;


public class BP {
    private final Drawable mPoint;
    private final String mText;

    public BP(Drawable point, String text) {
        mPoint = point;
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public Drawable getPoint() {
        return mPoint;
    }
}
