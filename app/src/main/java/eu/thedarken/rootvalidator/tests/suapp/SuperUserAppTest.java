package eu.thedarken.rootvalidator.tests.suapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.tests.ATest;
import eu.thedarken.rootvalidator.tools.Logy;

/**
 * Created by darken on 14.02.2015.
 */
public class SuperUserAppTest extends ATest{

    private static final String TAG = "RV:SuperUserAppTest";
    private static final String PKG_SUPERUSER_CHAINSDD = "com.noshufou.android.su";
    private static final String PKG_SUPERUSER_CHAINFIRE = "eu.chainfire.supersu";
    private static final String PKG_SUPERUSER_KOUSH = "com.koushikdutta.superuser";
    private static final String PKG_SUPERUSER_KINGROOT = "com.kingroot.kinguser";
    private static final String PKG_SUPERUSER_KINGOROOT = "com.kingouser.com";

    public SuperUserAppTest(Context context) {
        super(context);
    }

    @Override
    public SuAppInfo test() {
        SuAppInfo result = new SuAppInfo();
        ArrayList<String> candidates = new ArrayList<>();
        candidates.add(PKG_SUPERUSER_CHAINSDD);
        candidates.add(PKG_SUPERUSER_CHAINFIRE);
        candidates.add(PKG_SUPERUSER_KOUSH);
        candidates.add(PKG_SUPERUSER_KINGROOT);
        candidates.add(PKG_SUPERUSER_KINGOROOT);
        try {
            List<PackageInfo> apps = getContext().getPackageManager().getInstalledPackages(0);
            for (PackageInfo app : apps) {
                for (String candidate : candidates) {
                    if (app.packageName.equals(candidate)) {
                        SuApp suApp = new SuApp(app.packageName);
                        suApp.mVersionName = app.versionName;
                        suApp.mVersionCode = app.versionCode;

                        suApp.mFirstInstallTime = app.firstInstallTime;
                        suApp.mLastUpdateTime = app.lastUpdateTime;

                        if (app.applicationInfo != null) {
                            suApp.mName = (String) app.applicationInfo.loadLabel(getContext().getPackageManager());
                            suApp.mPrimaryPath = new File(app.applicationInfo.publicSourceDir);

                            suApp.mSystemApp = (app.applicationInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
                            if (suApp.mSystemApp && !app.applicationInfo.publicSourceDir.startsWith("/system/")) {
                                // Find secondary apk path in system
                                File systemAppDir = new File("/system/app/");
                                File[] systemAppFiles = systemAppDir.listFiles();
                                if (systemAppFiles != null) {
                                    for (File file : systemAppFiles) {
                                        if (!file.getName().endsWith(".apk"))
                                            continue;
                                        PackageInfo resolvedPkgInfo = getContext().getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 0);
                                        if (resolvedPkgInfo != null && suApp.getPackageName().equals(resolvedPkgInfo.packageName)) {
                                            suApp.mSecondaryPath = file;
                                        }
                                    }
                                }
                            }
                        }
                        result.getSuperUserApps().add(suApp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (SuApp suApp : result.getSuperUserApps())
            Logy.d(TAG, suApp.toString());
        return result;
    }
}
