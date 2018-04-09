/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.suapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import eu.darken.rxshell.cmd.Cmd;
import eu.darken.rxshell.cmd.RxCmdShell;
import eu.darken.rxshell.root.SuApp;
import eu.darken.rxshell.root.SuBinary;
import eu.thedarken.rootvalidator.main.core.TestResult;
import eu.thedarken.rootvalidator.main.core.TestSuite;
import io.reactivex.Single;


public class SuperUserAppTestSuite extends TestSuite {

    @Inject
    public SuperUserAppTestSuite(Context context) {
        super(context);
    }

    @Override
    public Single<List<TestResult>> test() {
        return Single.create(emitter -> {
            final SuperUserBinaryResult binaryResult = getBinaryResult();
            final SuperUserAppResult appResult = getAppResult(binaryResult);
            emitter.onSuccess(Arrays.asList(binaryResult, appResult));
        });
    }

    SuperUserBinaryResult getBinaryResult() {
        SuperUserBinaryResult.Builder binaryBuilder = new SuperUserBinaryResult.Builder();
        Set<String> suBinaryLocations = new HashSet<>();
        Set<String> possibleLocations = new HashSet<>();
        final RxCmdShell shell = RxCmdShell.builder().build();

        final Cmd.Result pathResult = Cmd.builder("echo $PATH").execute(shell);

        if (pathResult.getExitCode() == Cmd.ExitCode.OK && pathResult.getOutput().size() == 1) {
            for (String s : Arrays.asList(pathResult.getOutput().get(0).split(":"))) {
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
            final Cmd.Result fileResult = Cmd.builder("ls " + s).execute(shell);
            if (fileResult.getExitCode() == Cmd.ExitCode.OK && fileResult.getOutput().size() > 0) {
                suBinaryLocations.add(s);
            }
        }

        String primarySuBinary = null;
        final Cmd.Result locatePrimaryResult = Cmd.builder("command -v su").execute(shell);
        if (locatePrimaryResult.getExitCode() == Cmd.ExitCode.OK && locatePrimaryResult.getOutput().size() == 1) {
            primarySuBinary = locatePrimaryResult.getOutput().get(0);
        }
        List<SuperUserBinary> binaries = new ArrayList<>();
        for (String suPath : suBinaryLocations) {
            boolean primary = suPath.equals(primarySuBinary);
            binaries.add(getBinary(suPath, primary));
        }
        binaryBuilder.binaries(binaries);
        return binaryBuilder.build();
    }

    private static final Pattern SUBINARY_PERMISSION_PATTERN = Pattern.compile("^([\\w-]+)\\s+([\\w]+)\\s+([\\w]+)(?:[\\W\\w]+)$");

    private SuperUserBinary getBinary(String path, boolean primary) {
        SuBinary.Type type = SuBinary.Type.UNKNOWN;
        String permission = null;
        String owner = null;
        String group = null;
        String version = null;
        String extra = null;
        final List<String> raw = new ArrayList<>();

        final RxCmdShell shell = new RxCmdShell.Builder().build();
        final Cmd.Result result = Cmd.builder("ls -l " + path).execute(shell);
        if (result.getExitCode() == Cmd.ExitCode.OK && result.getOutput().size() > 0) {
            Matcher matcher = SUBINARY_PERMISSION_PATTERN.matcher(result.getOutput().get(0));
            if (matcher.matches()) {
                permission = matcher.group(1);
                owner = matcher.group(2);
                group = matcher.group(3);
            }
        }

        Cmd.Result versionResult = Cmd.builder("su --version").execute(shell);
        if (versionResult.getExitCode() != Cmd.ExitCode.OK && versionResult.getExitCode() != 127) {
            versionResult = Cmd.builder("su --V", "su -version", "su -v", "su -V").execute(shell);
        }

        if (versionResult.getExitCode() == Cmd.ExitCode.OK) {
            raw.addAll(versionResult.getOutput());
            for (String line : versionResult.getOutput()) {
                for (Map.Entry<Pattern, SuBinary.Type> entry : SuBinary.Builder.PATTERNMAP.entrySet()) {
                    Matcher matcher = entry.getKey().matcher(line);
                    if (matcher.matches()) {
                        type = entry.getValue();
                        if (matcher.groupCount() == 1) {
                            version = matcher.group(1);
                        } else if (matcher.groupCount() == 2) {
                            extra = matcher.group(2);
                        }
                        break;
                    }
                }
            }
        }
        return new SuperUserBinary(type, path, version, extra, raw, primary, permission, owner, group);
    }

    SuperUserAppResult getAppResult(SuperUserBinaryResult binaryResult) {
        SuperUserAppResult.Builder appBuilder = new SuperUserAppResult.Builder();

        SuperUserBinary.Type binaryType = SuBinary.Type.NONE;
        if (binaryResult.getPrimary() != null) binaryType = binaryResult.getPrimary().getType();

        final List<SuperUserApp> apps = new ArrayList<>();
        for (Map.Entry<eu.darken.rxshell.root.SuBinary.Type, String[]> entry : SuApp.Builder.SUAPPS.entrySet()) {

            // Otherwise we show the settings app as superuser app on stock ROMs
            if (binaryType != SuBinary.Type.CYANOGENMOD && entry.getKey() == SuBinary.Type.CYANOGENMOD) {
                continue;
            }

            for (String pkg : entry.getValue()) {
                try {
                    PackageInfo app = getContext().getPackageManager().getPackageInfo(pkg, PackageManager.GET_UNINSTALLED_PACKAGES);

                    String name = null;
                    String apkPath = null;
                    boolean systemApp = false;
                    boolean isPrimary = binaryType == entry.getKey();
                    if (app.applicationInfo != null) {
                        name = (String) app.applicationInfo.loadLabel(getContext().getPackageManager());
                        apkPath = app.applicationInfo.publicSourceDir;
                        systemApp = (app.applicationInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
                    }

                    SuperUserApp suApp = new SuperUserApp(
                            SuperUserBinary.Type.UNKNOWN,
                            app.packageName,
                            app.versionName,
                            app.versionCode,
                            apkPath,
                            name,
                            systemApp,
                            isPrimary
                    );
                    apps.add(suApp);
                } catch (PackageManager.NameNotFoundException ignored) {
                }
            }
        }
        appBuilder.apps(apps);
        return appBuilder.build();
    }
}
