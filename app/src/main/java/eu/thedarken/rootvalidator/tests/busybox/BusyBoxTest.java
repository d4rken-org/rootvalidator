/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests.busybox;

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

public class BusyBoxTest extends ATest {
    private static final String TAG = "RV:BusyboxTest";
    private List<String> mRaw;

    public BusyBoxTest(Context context) {
        super(context);
    }

    @Override
    public BusyBoxInfo test() {
        BusyBoxInfo result = new BusyBoxInfo();
        mRaw = result.getRaw();
        result.getBusyBoxes().addAll(locateBusyboxes());
        for (BusyBox busybox : result.getBusyBoxes()) {
            checkBusybox(busybox);
            Logy.d(TAG, busybox.toString());
        }
        return result;
    }

    private static final Pattern BUSYBOX_PERMISSION_PATTERN = Pattern.compile("^([\\w-]+)\\s+([\\w]+)\\s+([\\w]+)(?:[\\W\\w]+)$");
    private static final Pattern BUSYBOX_VERSION_PATTERN = Pattern.compile("^(?i:busybox)\\s([\\W\\w]+)\\s(?:\\([\\W\\w]+\\))\\s(?:multi-call binary.)$");

    private static final Pattern TYPE_LOCATE_PATTERN = Pattern.compile("^(?:busybox)\\s(?:[\\w\\s]+)\\s((?:\\/[\\w]+)+)$");

    private List<BusyBox> locateBusyboxes() {
        HashSet<String> busyBoxBinaryLocations = new HashSet<>();
        HashSet<String> possibleBinaryLocations = new HashSet<>();

        Cmd cmd = new Cmd();
        cmd.setRaw(mRaw);
        cmd.addCommand("echo $PATH");
        cmd.execute();

        if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() == 1) {
            for (String s : Arrays.asList(cmd.getOutput().get(0).split(":"))) {
                possibleBinaryLocations.add(s + "/busybox");
            }
        }

        for (String s : possibleBinaryLocations) {
            cmd.clearCommands();
            cmd.addCommand("ls" + s + "/busybox");
            cmd.execute();
            if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() > 0) {
                String path = s + "/busybox";
                busyBoxBinaryLocations.add(path);
            }
        }

        // Wasn't in $PATH, where then?
        possibleBinaryLocations.clear();
        possibleBinaryLocations.add("/sbin/busybox");
        possibleBinaryLocations.add("/system/bin/busybox");
        possibleBinaryLocations.add("/system/xbin/busybox");
        possibleBinaryLocations.add("/system/bin/failsafe/busybox");
        possibleBinaryLocations.add("/system/sd/xbin/busybox");
        possibleBinaryLocations.add("/data/local/busybox");
        possibleBinaryLocations.add("/data/local/bin/busybox");
        possibleBinaryLocations.add("/data/local/xbin/busybox");

        for (String s : possibleBinaryLocations) {
            cmd.clearCommands();
            cmd.addCommand("ls " + s);
            cmd.execute();
            if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() > 0) {
                busyBoxBinaryLocations.add(s);
            }
        }

        String primaryBusybox = null;
        cmd.clearCommands();
        cmd.addCommand("busybox");
        cmd.execute();
        if (cmd.getExitCode() == Cmd.OK) {
            // Busybox was in path
            cmd.clearCommands();
            cmd.addCommand("type busybox");
            cmd.execute();
            if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() > 0) {
                Matcher matcher = TYPE_LOCATE_PATTERN.matcher(cmd.getOutput().get(0));
                if (matcher.matches()) {
                    busyBoxBinaryLocations.add(matcher.group(1));
                    primaryBusybox = matcher.group(1);
                }
            }
        }
        cmd.clearCommands();
        List<BusyBox> busyboxes = new ArrayList<>();
        for (String bbPath : busyBoxBinaryLocations)
            busyboxes.add(new BusyBox(new File(bbPath), bbPath.equals(primaryBusybox)));
        return busyboxes;
    }

    private void checkBusybox(BusyBox binary) {
        Cmd cmd = new Cmd();
        cmd.setRaw(mRaw);
        cmd.addCommand("ls -l " + binary.getPath().getAbsolutePath());
        cmd.execute();

        if (cmd.getExitCode() == Cmd.OK && cmd.getOutput().size() > 0) {
            Matcher matcher = BUSYBOX_PERMISSION_PATTERN.matcher(cmd.getOutput().get(0));
            if (matcher.matches()) {
                binary.mPermission = matcher.group(1);
                binary.mOwner = matcher.group(2);
                binary.mGroup = matcher.group(3);
            }
        }

        cmd.clearCommands();
        cmd.addCommand(binary.getPath().getAbsolutePath());
        cmd.execute();

        if (cmd.getExitCode() != Cmd.OK) {
            binary.mExecutable = false;
            return;
        }
        if (cmd.getOutput().size() == 0)
            return;

        Matcher versionMatcher = BUSYBOX_VERSION_PATTERN.matcher(cmd.getOutput().get(0));
        if (versionMatcher.matches()) {
            binary.mVersion = versionMatcher.group(1);
        }

        int cmdstart = 0;
        // Skip first rubish lines
        while (cmdstart < 25) {
            if (cmd.getOutput().get(cmdstart).contains("Currently defined functions")) {
                break;
            }
            cmdstart++;
        }
        // Okay, start glueing the found cmds toegether
        StringBuilder gluedlines = new StringBuilder();
        for (String s : cmd.getOutput().subList(++cmdstart, cmd.getOutput().size())) {
            gluedlines.append(s);
        }

        String listedApplets = gluedlines.toString();

        int keep = 0;
        // Now check which applets are contained in the glued string
        while (keep < binary.getMissingApplets().size()) {
            if (!listedApplets.contains(binary.getMissingApplets().get(keep))) {
                keep++;
            } else {
                binary.getAvailableApplets().add(binary.getMissingApplets().remove(keep));
            }
        }
    }
}
