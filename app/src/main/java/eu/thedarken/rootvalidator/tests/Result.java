package eu.thedarken.rootvalidator.tests;

import android.os.Parcelable;

/**
 * Created by darken on 19.02.2015.
 */
public abstract class Result implements Parcelable {
    public enum Outcome {
        POSITIVE, NEUTRAL, NEGATIVE
    }
}
