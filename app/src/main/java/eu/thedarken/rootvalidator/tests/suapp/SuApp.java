package eu.thedarken.rootvalidator.tests.suapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import eu.thedarken.rootvalidator.tests.busybox.BusyBoxInfo;

/**
 * Created by darken on 19.02.2015.
 */
public class SuApp implements Parcelable {
    private final String mPackageName;
    String mVersionName;
    int mVersionCode;
    String mName;
    File mPrimaryPath;
    File mSecondaryPath;

    long mFirstInstallTime;
    long mLastUpdateTime;

    boolean mSystemApp = false;

    public SuApp(String packageName) {
        this.mPackageName = packageName;
    }

    public String getName() {
        return mName;
    }

    public boolean isSystemApp() {
        return mSystemApp;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getVersionName() {
        return mVersionName;
    }

    public int getVersionCode() {
        return mVersionCode;
    }

    public File getPrimaryPath() {
        return mPrimaryPath;
    }

    public File getSecondaryPath() {
        return mSecondaryPath;
    }

    public long getFirstInstallTime() {
        return mFirstInstallTime;
    }

    public long getLastUpdateTime() {
        return mLastUpdateTime;
    }

    @Override
    public String toString() {
        return "mPackageName:" + mPackageName + " | " +
                "mVersionName:" + mVersionName + " | " +
                "mVersionCode:" + mVersionCode + " | " +
                "mName:" + mName + " | " +
                "mPrimaryPath:" + mPrimaryPath + " | " +
                "mSecondaryPath:" + mSecondaryPath + " | " +
                "mFirstInstallTime:" + mFirstInstallTime + " | " +
                "mLastUpdateTime:" + mLastUpdateTime + " | " +
                "mSystemApp:" + mSystemApp;
    }

    public SuApp(Parcel in) {
        mPackageName = in.readString();
        mVersionName = in.readString();
        mVersionCode = in.readInt();
        mName = in.readString();
        mPrimaryPath = new File(in.readString());
        String secondaryPath = in.readString();
        if (!secondaryPath.equals("NULL"))
            mSecondaryPath = new File(secondaryPath);
        mFirstInstallTime = in.readLong();
        mLastUpdateTime = in.readLong();
        mSystemApp = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mPackageName);
        out.writeString(mVersionName);
        out.writeInt(mVersionCode);
        out.writeString(mName);
        out.writeString(mPrimaryPath.getAbsolutePath());
        if (mSecondaryPath != null)
            out.writeString(mSecondaryPath.getAbsolutePath());
        else
            out.writeString("NULL");
        out.writeLong(mFirstInstallTime);
        out.writeLong(mLastUpdateTime);
        out.writeByte((byte) (mSystemApp ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SuApp> CREATOR = new BusyBoxInfo.Creator<SuApp>() {
        public SuApp createFromParcel(Parcel in) {
            return new SuApp(in);
        }

        public SuApp[] newArray(int size) {
            return new SuApp[size];
        }
    };
}
