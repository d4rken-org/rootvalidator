package eu.thedarken.rootvalidator.tests.suapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import eu.thedarken.rootvalidator.tests.busybox.BusyBoxInfo;
import eu.thedarken.rootvalidator.tests.subinary.SuBinary;


public class SuApp implements Parcelable {
    private final String mPackageName;
    String mVersionName;
    int mVersionCode;
    String mName;
    File mPath;
    SuBinary.Type mType;
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

    public File getPath() {
        return mPath;
    }

    public SuBinary.Type getType() {
        return mType;
    }

    @Override
    public String toString() {
        return "mPackageName:" + mPackageName + " | " +
                "mVersionName:" + mVersionName + " | " +
                "mVersionCode:" + mVersionCode + " | " +
                "mName:" + mName + " | " +
                "mPrimaryPath:" + mPath + " | " +
                "mSystemApp:" + mSystemApp;
    }

    public SuApp(Parcel in) {
        mPackageName = in.readString();
        mVersionName = in.readString();
        mVersionCode = in.readInt();
        mName = in.readString();
        mPath = new File(in.readString());
        mSystemApp = in.readByte() != 0;
        mType = SuBinary.Type.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mPackageName);
        out.writeString(mVersionName);
        out.writeInt(mVersionCode);
        out.writeString(mName);
        out.writeString(mPath.getAbsolutePath());
        out.writeByte((byte) (mSystemApp ? 1 : 0));
        out.writeString(mType.name());
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
