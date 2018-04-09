package eu.thedarken.rootvalidator.main.core;

import android.content.Context;

public interface Result {
    Outcome getOutcome();

    String getPrimaryInfo(Context context);
}
