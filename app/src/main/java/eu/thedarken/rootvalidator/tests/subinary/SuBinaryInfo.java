package eu.thedarken.rootvalidator.tests.subinary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.tests.BP;
import eu.thedarken.rootvalidator.tests.Result;
import eu.thedarken.rootvalidator.tests.TestInfo;


public class SuBinaryInfo extends TestInfo {
    private final List<SuBinary> mSuBinaries = new ArrayList<>();

    public SuBinaryInfo() {
        super("SuBinary Test");
    }

    public SuBinary getPrimary() {
        for (SuBinary suBinary : mSuBinaries) {
            if (suBinary.isPrimary()) {
                return suBinary;
            }
        }
        return null;
    }

    public List<SuBinary> getSuBinaries() {
        return mSuBinaries;
    }

    @Override
    public Result.Outcome getOutcome() {
        if (getSuBinaries().isEmpty() || (getSuBinaries().size() == 1 && getPrimary() == null)) {
            return Result.Outcome.NEGATIVE;
        } else if (getSuBinaries().size() > 1) {
            return Result.Outcome.NEUTRAL;
        } else {
            return Result.Outcome.POSITIVE;
        }
    }

    @Override
    public String getPrimaryInfo(Context context) {
        return "Superuser binary";
    }

    @Override
    public List<BP> getCriterias(Context context) {
        List<BP> criterias = new ArrayList<>();
        SuBinary primSU = getPrimary();
        if (primSU != null) {
            criterias.add(new BP(getPositiveD(context), "su binary is available via $PATH."));
        } else {
            criterias.add(new BP(getNegativeD(context), "su binary not available via $PATH."));
        }
        for (SuBinary sub : mSuBinaries) {
            Drawable d = getNeutralD(context);
            if (sub.isPrimary())
                d = getPositiveD(context);
            String output = (sub.getType() + "\n")
                    + sub.getVersion() + " " + sub.getExtra() + "\n"
                    + sub.getPermission() + " " + sub.getOwner() + " " + sub.getGroup() + "\n"
                    + sub.getPath().getAbsolutePath();
            criterias.add(new BP(d, output));
        }
        return criterias;
    }

    public SuBinaryInfo(Parcel in) {
        this();
        in.readList(mSuBinaries, SuBinary.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeList(mSuBinaries);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SuBinaryInfo> CREATOR = new Creator<SuBinaryInfo>() {
        public SuBinaryInfo createFromParcel(Parcel in) {
            return new SuBinaryInfo(in);
        }

        public SuBinaryInfo[] newArray(int size) {
            return new SuBinaryInfo[size];
        }
    };
}
