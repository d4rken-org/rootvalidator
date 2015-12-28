/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests.selinux;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.tests.BP;
import eu.thedarken.rootvalidator.tests.Result;
import eu.thedarken.rootvalidator.tests.TestInfo;
import eu.thedarken.rootvalidator.tools.ApiHelper;

public class SELinuxInfo extends TestInfo {
    private State mState = State.UNKNOWN;

    public enum State {
        UNKNOWN, DISABLED, PERMISSIVE, ENFORCING
    }

    public SELinuxInfo() {
        super("SELinux Test");
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

    @Override
    public Result.Outcome getOutcome() {
        if (getState() == State.ENFORCING) {
            return Result.Outcome.NEUTRAL;
        } else if (getState() == State.UNKNOWN && ApiHelper.hasJellyBeanMR1()) {
            return Result.Outcome.NEGATIVE;
        } else {
            return Result.Outcome.POSITIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (getState() == State.ENFORCING) {
            return "SELinux is enforcing";
        } else if (getState() == State.PERMISSIVE) {
            return "SELinux is permissive";
        } else if (getState() == State.DISABLED) {
            return "SELinux is disabled";
        } else {
            return "SELinux state is unknown";
        }
    }

    @Override
    public List<BP> getCriterias(Context context) {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "mState:" + mState.name();
    }

    public SELinuxInfo(Parcel in) {
        this();
        mState = State.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mState.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SELinuxInfo> CREATOR = new Creator<SELinuxInfo>() {
        public SELinuxInfo createFromParcel(Parcel in) {
            return new SELinuxInfo(in);
        }

        public SELinuxInfo[] newArray(int size) {
            return new SELinuxInfo[size];
        }
    };
}
