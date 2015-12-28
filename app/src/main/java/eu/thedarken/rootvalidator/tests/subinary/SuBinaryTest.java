package eu.thedarken.rootvalidator.tests.subinary;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.thedarken.rootvalidator.tests.ATest;
import eu.thedarken.rootvalidator.tools.Cmd;
import eu.thedarken.rootvalidator.tools.Logy;

public class SuBinaryTest extends ATest {
    private static final String TAG = "RV:SuBinaryTest";
    private static final Map<Pattern, SuBinary.Type> PATTERNMAP;

    static {
        PATTERNMAP = new HashMap<>();
        // Chainfire SU "2.25:SUPERSU"
        PATTERNMAP.put(Pattern.compile("^([0-9\\.]*):(SUPERSU)$"), SuBinary.Type.CHAINFIRE_SUPERSU);
        // Koush SU "16 com.koushikdutta.superuser"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.koushikdutta\\.superuser)$"), SuBinary.Type.KOUSH_SUPERUSER);
        // KingUser "3.43:kinguser_su"
        PATTERNMAP.put(Pattern.compile("^([0-9\\.]*):(kinguser_su)$"), SuBinary.Type.KINGUSER);
        // KingoRoot "13 com.kingouser.com"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.kingouser\\.com)$"), SuBinary.Type.KINGOUSER);
        // Cyanogen Mod e.g. "16 com.android.settings"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.android\\.settings)$"), SuBinary.Type.CYANOGENMOD);
        // Cyanogen Mod clone e.g. "16 cm-su"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(cm-su)$"), SuBinary.Type.CYANOGENMOD);
        // ChainsDD "3.3" or "3.1l" or "2.3.1-abcdefgh" etc.
        PATTERNMAP.put(Pattern.compile("^(3\\.(?:3|2|1|0))(l?)$"), SuBinary.Type.CHAINSDD_SUPERUSER);
        PATTERNMAP.put(Pattern.compile("^(3\\.0)-(beta2)$"), SuBinary.Type.CHAINSDD_SUPERUSER);
        PATTERNMAP.put(Pattern.compile("^(3\\.1\\.1)(l?)$"), SuBinary.Type.CHAINSDD_SUPERUSER);
        PATTERNMAP.put(Pattern.compile("^(3\\.0\\.3\\.2)(l?)$"), SuBinary.Type.CHAINSDD_SUPERUSER);
        PATTERNMAP.put(Pattern.compile("^(3\\.0\\.(?:3|2|1))(l?)$"), SuBinary.Type.CHAINSDD_SUPERUSER);
        PATTERNMAP.put(Pattern.compile("^(2.3.(?:1|2))(-[abcdefgh]{1,8})$"), SuBinary.Type.CHAINSDD_SUPERUSER);
        // VROOT "11 com.mgyun.shua.su"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.mgyun\\.shua\\.su)$"), SuBinary.Type.VROOT);
        // VenomSU, TEAM Venom "Venom SuperUser v21"
        PATTERNMAP.put(Pattern.compile("^(?:Venom\\WSuperUser)\\W(v[0-9]+)$"), SuBinary.Type.VENOMSU);
        // Qihoo 360 "360.cn es 1.6.0.6" com.qihoo.permmgr
        PATTERNMAP.put(Pattern.compile("^(360\\Wcn\\Wes)\\W?([0-9\\.]+)$"), SuBinary.Type.QIHOO_360);
        // MIUI "15 com.lbe.security.miui"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.lbe\\.security\\.miui)$"), SuBinary.Type.MIUI);
        // Baidu Easyroot "15 com.baidu.easyroot"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.baidu\\.easyroot)$"), SuBinary.Type.BAIDU_EASYROOT);
        // Koush SuperUser clone "26 com.dianxinos.superuser"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.dianxinos\\.superuser)$"), SuBinary.Type.DIANXINOSSUPERUSER);
        // Koush SuperUser clone "16 com.baiyi_mobile.easyroot"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.baiyi_mobile\\.easyroot)$"), SuBinary.Type.BAIYI_MOBILE_EASYROOT);
        // CyanogenMod SuperUser clone "16 com.tencent.qrom.appmanager"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(com\\.tencent\\.qrom\\.appmanager)$"), SuBinary.Type.TENCENT_APPMANAGER);
        // https://github.com/seSuperuser/Superuser "16 me.phh.superuser cm-su"
        PATTERNMAP.put(Pattern.compile("^([0-9]*)\\W(me\\.phh\\.superuser)\\W([\\W\\w]*)$"), SuBinary.Type.SE_SUPERUSER);
    }

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
        cmd.setTimeout(5000);
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
        cmd.addCommand("su --version");
        cmd.execute();
        if (cmd.getExitCode() != Cmd.OK && cmd.getExitCode() != Cmd.COMMAND_NOT_FOUND) {
            cmd.clearCommands();
            cmd.addCommand("su --V");
            cmd.addCommand("su -version");
            cmd.addCommand("su -v");
            cmd.addCommand("su -V");
            cmd.execute();
        }
        if (cmd.getExitCode() == Cmd.OK) {
            binary.mType = SuBinary.Type.UNKNOWN;
            for (String line : cmd.getOutput()) {

                for (Map.Entry<Pattern, SuBinary.Type> entry : PATTERNMAP.entrySet()) {
                    Matcher matcher = entry.getKey().matcher(line);
                    if (matcher.matches()) {
                        binary.mType = entry.getValue();
                        binary.mVersion = matcher.group(1);
                        binary.mExtra = matcher.group(2);
                        break;
                    }
                }
            }
        }
    }

}
