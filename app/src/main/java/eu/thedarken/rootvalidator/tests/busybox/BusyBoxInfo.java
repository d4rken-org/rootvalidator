/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests.busybox;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.tests.BP;
import eu.thedarken.rootvalidator.tests.Result;
import eu.thedarken.rootvalidator.tests.TestInfo;

public class BusyBoxInfo extends TestInfo {
    private final List<BusyBox> mBusyBoxes = new ArrayList<>();

    public BusyBoxInfo() {
        super("BusyBox Test");
    }

    public List<BusyBox> getBusyBoxes() {
        return mBusyBoxes;
    }

    public BusyBox getPrimary() {
        for (BusyBox bb : mBusyBoxes) {
            if (bb.isPrimary()) {
                return bb;
            }
        }
        return null;
    }

    @Override
    public Result.Outcome getOutcome() {
        if (getPrimary() == null || !getPrimary().isSufficient()) {
            return Result.Outcome.NEGATIVE;
        } else {
            return Result.Outcome.POSITIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (getBusyBoxes().size() == 0) {
            return "No busybox";
        } else if (getBusyBoxes().size() == 1) {
            return "Busybox found";
        } else {
            return "Multiple busybox's";
        }
    }

    @Override
    public List<BP> getCriterias(Context context) {
        List<BP> criterias = new ArrayList<>();
        BusyBox primBB = getPrimary();
        if (primBB != null) {
            criterias.add(new BP(getPositiveD(context), "Primary busybox is avaiable via $PATH."));
            if (primBB.isSufficient()) {
                criterias.add(new BP(getPositiveD(context), "Primary busybox offers most common applets."));
            } else {
                criterias.add(new BP(getNegativeD(context), "Primary busybox may lack some common applets."));
            }
        } else {
            criterias.add(new BP(getNegativeD(context), "No busybox avaiable via $PATH."));
        }
        for (BusyBox bb : mBusyBoxes) {
            Drawable d = getNeutralD(context);
            if (bb.isPrimary())
                d = getPositiveD(context);
            if (bb.getAvailableApplets().isEmpty())
                d = getNegativeD(context);
            String builder = (bb.getVersion() + ", " + bb.getAvailableApplets().size() + " applets.\n")
                    + bb.getPermission() + " " + bb.getOwner() + " " + bb.getGroup() + "\n"
                    + bb.getPath().getAbsolutePath();
            criterias.add(new BP(d, builder));
        }
        return criterias;
    }

    public BusyBoxInfo(Parcel in) {
        this();
        in.readList(mBusyBoxes, BusyBox.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(mBusyBoxes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BusyBoxInfo> CREATOR = new Creator<BusyBoxInfo>() {
        public BusyBoxInfo createFromParcel(Parcel in) {
            return new BusyBoxInfo(in);
        }

        public BusyBoxInfo[] newArray(int size) {
            return new BusyBoxInfo[size];
        }
    };

}
