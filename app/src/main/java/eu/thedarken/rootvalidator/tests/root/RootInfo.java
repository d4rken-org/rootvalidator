/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests.root;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.tests.BP;
import eu.thedarken.rootvalidator.tests.Result;
import eu.thedarken.rootvalidator.tests.TestInfo;
import eu.thedarken.rootvalidator.tests.busybox.BusyBoxInfo;
import eu.thedarken.rootvalidator.tools.Cmd;

public class RootInfo extends TestInfo {
    boolean mGotRoot = false;
    boolean mSuLaunchesShell = false;
    boolean mBinaryIssue = false;
    int mExitCode = 99;

    public RootInfo() {
        super("General Root Test");
    }

    @Override
    public Result.Outcome getOutcome() {
        if (mGotRoot) {
            return Result.Outcome.POSITIVE;
        } else if (!mBinaryIssue) {
            return Result.Outcome.NEUTRAL;
        } else {
            return Result.Outcome.NEGATIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (mGotRoot) {
            return "Root available!";
        } else if (!mBinaryIssue) {
            return "Root denied?";
        } else {
            return "Root unavailable";
        }
    }

    @Override
    public List<BP> getCriterias(Context context) {
        List<BP> criterias = new ArrayList<>();
        if (mBinaryIssue)
            criterias.add(new BP(getNegativeD(context), "su/id/echo did not execute."));
        if (mSuLaunchesShell)
            criterias.add(new BP(getPositiveD(context), "su binary launches a shell."));
        if (mGotRoot)
            criterias.add(new BP(getPositiveD(context), "Root UID obtained."));
        if (mExitCode != Cmd.OK)
            criterias.add(new BP(getNegativeD(context), "Root shell had non OK exitcode (" + mExitCode + ")."));
        return criterias;
    }

    @Override
    public String toString() {
        return "mGotRoot:" + mGotRoot + " | " +
                "mSuLaunchesShell:" + mSuLaunchesShell + " | " +
                "mBinaryIssue:" + mBinaryIssue;
    }

    public RootInfo(Parcel in) {
        this();
        mGotRoot = in.readByte() != 0;
        mSuLaunchesShell = in.readByte() != 0;
        mBinaryIssue = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeByte((byte) (mGotRoot ? 1 : 0));
        out.writeByte((byte) (mSuLaunchesShell ? 1 : 0));
        out.writeByte((byte) (mBinaryIssue ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<RootInfo> CREATOR = new BusyBoxInfo.Creator<RootInfo>() {
        public RootInfo createFromParcel(Parcel in) {
            return new RootInfo(in);
        }

        public RootInfo[] newArray(int size) {
            return new RootInfo[size];
        }
    };

}
