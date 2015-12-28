/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests;

import android.os.Parcelable;


public abstract class Result implements Parcelable {
    public enum Outcome {
        POSITIVE, NEUTRAL, NEGATIVE
    }
}
