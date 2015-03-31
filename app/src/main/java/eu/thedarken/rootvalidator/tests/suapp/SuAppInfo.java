package eu.thedarken.rootvalidator.tests.suapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.tests.BP;
import eu.thedarken.rootvalidator.tests.Result;
import eu.thedarken.rootvalidator.tests.TestInfo;

/**
 * Created by darken on 14.02.2015.
 */
public class SuAppInfo extends TestInfo {
    private final ArrayList<SuApp> mSuApps = new ArrayList<>();

    public SuAppInfo() {
        super("SuperUser App Test");
    }

    public List<SuApp> getSuperUserApps() {
        return mSuApps;
    }

    @Override
    public Result.Outcome getOutcome() {
        if (getSuperUserApps().size() > 1) {
            return Result.Outcome.NEUTRAL;
        } else if (getSuperUserApps().size() == 1) {
            return Result.Outcome.POSITIVE;
        } else {
            return Result.Outcome.NEGATIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        if (getSuperUserApps().size() > 1) {
            return "Multiple superuser apps";
        } else if (getSuperUserApps().size() == 1) {
            return "Superuser app found";
        } else {
            return "No superuser app found";
        }
    }

    @Override
    public List<BP> getCriterias(Context context) {
        List<BP> criterias = new ArrayList<>();
        for (SuApp suApp : mSuApps) {
            StringBuilder builder = new StringBuilder();
            builder.append(suApp.getPackageName() + " @ " + suApp.getVersionName() + " (" + suApp.getVersionCode() + ")");
            builder.append("\n" + suApp.getPrimaryPath().getAbsolutePath());
            if (suApp.getSecondaryPath() != null)
                builder.append("\n" + suApp.getSecondaryPath().getAbsolutePath());
            criterias.add(new BP(getPositiveD(context), builder.toString()));
        }
        return criterias;
    }

    public SuAppInfo(Parcel in) {
        this();
        in.readList(mSuApps, SuApp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(mSuApps);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SuAppInfo> CREATOR = new Creator<SuAppInfo>() {
        public SuAppInfo createFromParcel(Parcel in) {
            return new SuAppInfo(in);
        }

        public SuAppInfo[] newArray(int size) {
            return new SuAppInfo[size];
        }
    };
}
