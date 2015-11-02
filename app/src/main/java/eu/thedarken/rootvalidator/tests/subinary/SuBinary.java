package eu.thedarken.rootvalidator.tests.subinary;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import eu.thedarken.rootvalidator.tests.busybox.BusyBoxInfo;

/**
 * Created by darken on 19.02.2015.
 */
public class SuBinary implements Parcelable {

    public enum Type {
        SUPERSU, SUPERUSER, KINGUSER, KINGOUSER, UNKNOWN
    }

    final File mPath;
    final boolean mPrimary;
    Type mType = Type.UNKNOWN;
    String mVersion;
    String mExtra;
    String mPermission;
    String mOwner;
    String mGroup;

    public SuBinary(File path, boolean primary) {
        this.mPath = path;
        this.mPrimary = primary;
    }

    public Type getType() {
        return mType;
    }

    public String getVersion() {
        return mVersion;
    }

    public String getExtra() {
        return mExtra;
    }

    public File getPath() {
        return mPath;
    }

    public String getPermission() {
        return mPermission;
    }

    public boolean isPrimary() {
        return mPrimary;
    }

    public String getOwner() {
        return mOwner;
    }

    public String getGroup() {
        return mGroup;
    }

    @Override
    public String toString() {
        return "mType:" + mType + " | " +
                "mVersion:" + mVersion + " | " +
                "mExtra:" + mExtra + " | " +
                "mOwner:" + mOwner + " | " +
                "mGroup:" + mGroup + " | " +
                "primary:" + mPrimary + " | " +
                "mPath:" + mPath + " | " +
                "mPermission:" + mPermission;
    }

    public SuBinary(Parcel in) {
        mPath = new File(in.readString());
        mPrimary = in.readByte() != 0;
        mType = Type.valueOf(in.readString());
        mVersion = in.readString();
        mExtra = in.readString();
        mPermission = in.readString();
        mOwner = in.readString();
        mGroup = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mPath.getAbsolutePath());
        out.writeByte((byte) (mPrimary ? 1 : 0));
        out.writeString(mType.name());
        out.writeString(mVersion);
        out.writeString(mExtra);
        out.writeString(mPermission);
        out.writeString(mOwner);
        out.writeString(mGroup);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SuBinary> CREATOR = new BusyBoxInfo.Creator<SuBinary>() {
        public SuBinary createFromParcel(Parcel in) {
            return new SuBinary(in);
        }

        public SuBinary[] newArray(int size) {
            return new SuBinary[size];
        }
    };

}
