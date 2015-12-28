/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tools;

import android.util.Log;

import eu.thedarken.rootvalidator.BuildConfig;

public class Logy {
    public static final int SILENT = -1;
    public static final int NORMAL = 0;
    public static final int DEBUG = 1;
    public static final int VERBOSE = 2;

    public static int sLoglevel = BuildConfig.DEBUG ? VERBOSE : NORMAL;

    public static void v(String c, String s) {
        if (sLoglevel >= VERBOSE) {
            if (s == null)
                s = "\"NULL\"";
            Log.v(c, s);
        }
    }

    public static void d(String c, String s) {
        if (sLoglevel >= DEBUG) {
            if (s == null)
                s = "\"NULL\"";
            Log.d(c, s);
        }
    }

    public static void i(String c, String s) {
        if (sLoglevel >= NORMAL) {
            if (s == null)
                s = "\"NULL\"";
            Log.i(c, s);
        }
    }

    public static void w(String c, String s) {
        if (sLoglevel >= SILENT) {
            if (s == null)
                s = "\"NULL\"";
            Log.w(c, s);
        }
    }

    public static void e(String c, String s) {
        if (sLoglevel >= SILENT) {
            if (s == null)
                s = "\"NULL\"";
            Log.e(c, s);
        }
    }

}
