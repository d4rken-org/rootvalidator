/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.applets;

import android.content.Context;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import eu.darken.rxshell.cmd.Cmd;
import eu.darken.rxshell.cmd.RxCmdShell;
import eu.thedarken.rootvalidator.main.core.TestResult;
import eu.thedarken.rootvalidator.main.core.TestSuite;
import io.reactivex.Single;

public class AppletBinaryTestSuite extends TestSuite {

    @Inject
    public AppletBinaryTestSuite(Context context) {
        super(context);
    }

    @Override
    public Single<List<TestResult>> test() {
        return Single.create(emitter -> {
            final AppletBinaryResult.Builder builder = new AppletBinaryResult.Builder();
            final RxCmdShell shell = RxCmdShell.builder().build();

            Cmd.Result result = Cmd.builder("echo $PATH").execute(shell);

            final HashSet<String> possibleBinaryLocations = new HashSet<>();
            if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() == 1) {
                for (String s : Arrays.asList(result.getOutput().get(0).split(":"))) {
                    possibleBinaryLocations.add(s + "/busybox");
                    possibleBinaryLocations.add(s + "/toybox");
                }
            }

            final HashSet<String> binaryLocations = new HashSet<>();
            for (String s : possibleBinaryLocations) {
                result = Cmd.builder("ls" + s + "/busybox").execute(shell);
                if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
                    binaryLocations.add(s + "/busybox");
                }
                result = Cmd.builder("ls" + s + "/toybox").execute(shell);
                if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
                    binaryLocations.add(s + "/toybox");
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

            possibleBinaryLocations.add("/sbin/toybox");
            possibleBinaryLocations.add("/system/bin/toybox");
            possibleBinaryLocations.add("/system/xbin/toybox");
            possibleBinaryLocations.add("/system/bin/failsafe/toybox");
            possibleBinaryLocations.add("/system/sd/xbin/toybox");
            possibleBinaryLocations.add("/data/local/toybox");
            possibleBinaryLocations.add("/data/local/bin/toybox");
            possibleBinaryLocations.add("/data/local/xbin/toybox");

            for (String s : possibleBinaryLocations) {
                result = Cmd.builder("ls " + s).execute(shell);
                if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
                    binaryLocations.add(s);
                }
            }

            String primaryBinary = null;
            result = Cmd.builder("command -v mount").execute(shell);
            if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
                String mountApplet = result.getOutput().get(0);
                result = Cmd.builder("stat -c %N " + mountApplet).execute(shell);
                if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
                    if (result.getOutput().get(0).contains("busybox")) {
                        result = Cmd.builder("command -v busybox").execute(shell);
                    } else {
                        result = Cmd.builder("command -v toybox").execute(shell);
                    }
                    if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
                        primaryBinary = result.getOutput().get(0);
                    }
                }
            }

            for (String path : binaryLocations) {
                builder.binaries(checkBinary(path, path.equals(primaryBinary)));
            }

            emitter.onSuccess(Collections.singletonList(builder.build()));
        });
    }

    private static final Pattern PERMISSION_PATTERN = Pattern.compile("^([\\w-]+)\\s+([\\w]+)\\s+([\\w]+)(?:[\\W\\w]+)$");
    private static final Pattern BUSYBOX_VERSION_PATTERN = Pattern.compile("^(?i:busybox)\\s([\\W\\w]+)\\s(?:\\([\\W\\w]+\\))\\s(?:multi-call binary.)$");


    private AppletBinary checkBinary(String path, boolean isPrimary) {
        String permission = null;
        String owner = null;
        String group = null;
        boolean isExecutable;
        String version = null;
        final RxCmdShell shell = RxCmdShell.builder().build();

        Cmd.Result result = Cmd.builder("ls -l " + path).execute(shell);

        if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
            Matcher matcher = PERMISSION_PATTERN.matcher(result.getOutput().get(0));
            if (matcher.matches()) {
                permission = matcher.group(1);
                owner = matcher.group(2);
                group = matcher.group(3);
            }
        }

        result = Cmd.builder(path).execute(shell);
        isExecutable = result.getExitCode() == Cmd.ExitCode.OK;

        if (result.getOutput().size() > 0) {
            Matcher versionMatcher = BUSYBOX_VERSION_PATTERN.matcher(result.getOutput().get(0));
            if (versionMatcher.matches()) {
                version = versionMatcher.group(1);
            }
        }

        if (version == null) {
            result = Cmd.builder(path + " --version").execute(shell);
            if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
                version = result.getOutput().get(0);
            }
        }

        return new AppletBinary(path, isPrimary, permission, owner, group, isExecutable, version);
    }
}
