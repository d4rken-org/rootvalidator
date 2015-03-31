package eu.thedarken.rootvalidator.tests;

import android.graphics.drawable.Drawable;

/**
 * Created by darken on 19.02.2015.
 */
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
