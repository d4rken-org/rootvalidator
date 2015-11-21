package eu.thedarken.rootvalidator.tests.subinary;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.thedarken.rootvalidator.tests.ATest;
import eu.thedarken.rootvalidator.tools.Cmd;
import eu.thedarken.rootvalidator.tools.Logy;

/**
 * Created by darken on 14.02.2015.
 */
public class SuBinaryTest extends ATest {
    private static final String TAG = "RV:SuBinaryTest";
    private static final Pattern KOUSH_SUPERUSER_PATTERN = Pattern.compile("^([0-9]{1,2})\\s([\\S]+)$");
    private static final Pattern CF_SUPERSU_PATTERN = Pattern.compile("^([0-9\\.]*):(SUPERSU)$");
    private static final Pattern KINGUSERSU_PATTERN = Pattern.compile("^([0-9\\.]*):(kinguser_su)$");
    private static final Pattern KINGOUSERSU_PATTERN = Pattern.compile("^([0-9\\.]*):(com.kingouser.com)$");

    private List<String> mRaw;

    public SuBinaryTest(Context context) {
        super(context);
    }

    @Override
    public SuBinaryInfo test() {
        SuBinaryInfo result = new SuBinaryInfo();
        mRaw = result.getRaw();
        result.getSuBinaries().addAll(locateSuBinaries());
        for (SuBinary binary : result.getSuBinaries()) {
            checkBinary(binary);
            Logy.d(TAG, binary.toString());
        }
        return result;
    }

    private static final Pattern SUBINARY_PERMISSION_PATTERN = Pattern.compile("^([\\w-]+)\\s+([\\w]+)\\s+([\\w]+)(?:[\\W\\w]+)$");


    private static final Pattern TYPE_LOCATE_PATTERN = Pattern.compile("^(?:su)\\s(?:[\\w\\s]+)\\s((?:\\/[\\w]+)+)$");

    private List<SuBinary> locateSuBinaries() {
        HashSet<String> suBinaryLocations = new HashSet<>();
        HashSet<String> possibleLocations = new HashSet<>();

        Cmd cmd = new Cmd();
        cmd.setRaw(mRaw);
        cmd.addCommand("echo $PATH");
        cmd.execute();
        if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() == 1) {
            for (String s : Arrays.asList(cmd.getOutput().get(0).split(":"))) {
                possibleLocations.add(s + "/su");
            }
        }

        // More possible locations
        possibleLocations.add("/sbin/su");
        possibleLocations.add("/system/bin/su");
        possibleLocations.add("/system/xbin/su");
        possibleLocations.add("/system/bin/failsafe/su");
        possibleLocations.add("/system/sd/xbin/su");
        possibleLocations.add("/data/local/su");
        possibleLocations.add("/data/local/bin/su");
        possibleLocations.add("/data/local/xbin/su");

        for (String s : possibleLocations) {
            cmd.clearCommands();
            cmd.addCommand("ls " + s);
            cmd.execute();
            if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() > 0) {
                suBinaryLocations.add(s);
            }
        }

        String primarySuBinary = null;
        cmd.clearCommands();
        cmd.addCommand("su --version");
        cmd.execute();
        if (cmd.getExitCode() == Cmd.OK) {
            // su binary was in mPath
            cmd.clearCommands();
            cmd.addCommand("type su");
            cmd.execute();
            if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() > 0) {
                Matcher matcher = TYPE_LOCATE_PATTERN.matcher(cmd.getOutput().get(0));
                if (matcher.matches()) {
                    suBinaryLocations.add(matcher.group(1));
                    primarySuBinary = matcher.group(1);
                }
            }
        }
        List<SuBinary> binaries = new ArrayList<>();
        for (String suPath : suBinaryLocations) {
            boolean primary = suPath.equals(primarySuBinary);
            binaries.add(new SuBinary(new File(suPath), primary));
        }
        return binaries;
    }

    private void checkBinary(SuBinary binary) {
        Cmd cmd = new Cmd();
        cmd.setRaw(mRaw);
        cmd.addCommand("ls -l " + binary.getPath().getAbsolutePath());
        cmd.execute();

        if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() > 0) {
            Matcher matcher = SUBINARY_PERMISSION_PATTERN.matcher(cmd.getOutput().get(0));
            if (matcher.matches()) {
                binary.mPermission = matcher.group(1);
                binary.mOwner = matcher.group(2);
                binary.mGroup = matcher.group(3);
            }
        }

        cmd.clearCommands();
        cmd.addCommand(binary.getPath().getAbsolutePath() + " --version");
        cmd.execute();
        if (cmd.getExitCode() == Cmd.OK) {
            for (String line : cmd.getOutput()) {
                // SUPERSU from Chainfire e.g. "2.25:SUPERSU"
                Matcher cfMatcher = CF_SUPERSU_PATTERN.matcher(line);
                if (cfMatcher.matches()) {
                    binary.mType = SuBinary.Type.SUPERSU;
                    binary.mVersion = cfMatcher.group(1);
                    binary.mExtra = cfMatcher.group(2);
                    break;
                }
                // SUPERUSER from Koush e.g. "16 com.android.settings"
                Matcher kuMatcher = KOUSH_SUPERUSER_PATTERN.matcher(line);
                if (kuMatcher.matches()) {
                    binary.mType = SuBinary.Type.SUPERUSER;
                    binary.mVersion = kuMatcher.group(1);
                    binary.mExtra = kuMatcher.group(2);
                    break;
                }
                // SUPERUSER from kinguser e.g. "3.43:kinguser_su"
                Matcher kingMatcher = KINGUSERSU_PATTERN.matcher(line);
                if (kingMatcher.matches()) {
                    binary.mType = SuBinary.Type.KINGUSER;
                    binary.mVersion = kingMatcher.group(1);
                    binary.mExtra = kingMatcher.group(2);
                    break;
                }
                // SUPERUSER from kingouser e.g. "13 com.kingouser.com"
                Matcher kingoMatcher = KINGOUSERSU_PATTERN.matcher(line);
                if (kingoMatcher.matches()) {
                    binary.mType = SuBinary.Type.KINGOUSER;
                    binary.mVersion = kingoMatcher.group(1);
                    binary.mExtra = kingoMatcher.group(2);
                    break;
                }
            }
        }
    }
}
