package eu.thedarken.rootvalidator.tests.suapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import eu.thedarken.rootvalidator.tests.ATest;
import eu.thedarken.rootvalidator.tests.subinary.SuBinary;
import eu.thedarken.rootvalidator.tests.subinary.SuBinaryInfo;
import eu.thedarken.rootvalidator.tools.Logy;


public class SuperUserAppTest extends ATest {

    private static final String TAG = "RV:SuperUserAppTest";
    private static final Map<SuBinary.Type, String[]> SUAPP_MAPPING;
    private final SuBinaryInfo mResultSuBinaryTest;

    static {
        SUAPP_MAPPING = new HashMap<>();
        SUAPP_MAPPING.put(SuBinary.Type.CHAINFIRE_SUPERSU, new String[]{"eu.chainfire.supersu"});
        SUAPP_MAPPING.put(SuBinary.Type.KOUSH_SUPERUSER, new String[]{"com.koushikdutta.superuser"});
        SUAPP_MAPPING.put(SuBinary.Type.CHAINSDD_SUPERUSER, new String[]{"com.noshufou.android.su"});
        SUAPP_MAPPING.put(SuBinary.Type.KINGUSER, new String[]{"com.kingroot.kinguser"});
        SUAPP_MAPPING.put(SuBinary.Type.VROOT, new String[]{"com.mgyun.shua.su", "com.mgyun.superuser"});
        SUAPP_MAPPING.put(SuBinary.Type.VENOMSU, new String[]{"com.m0narx.su"});
        SUAPP_MAPPING.put(SuBinary.Type.KINGOUSER, new String[]{"com.kingouser.com"});
        SUAPP_MAPPING.put(SuBinary.Type.MIUI, new String[]{"com.miui.uac", "com.lbe.security.miui"});
        SUAPP_MAPPING.put(SuBinary.Type.CYANOGENMOD, new String[]{"com.android.settings"});
        SUAPP_MAPPING.put(SuBinary.Type.QIHOO_360, new String[]{"com.qihoo.permmgr"});
        SUAPP_MAPPING.put(SuBinary.Type.QIHOO_360, new String[]{"com.qihoo.permroot"});
        SUAPP_MAPPING.put(SuBinary.Type.MIUI, new String[]{"com.lbe.security.miui"});
        SUAPP_MAPPING.put(SuBinary.Type.BAIDU_EASYROOT, new String[]{"com.baidu.easyroot"});
        SUAPP_MAPPING.put(SuBinary.Type.DIANXINOSSUPERUSER, new String[]{"com.dianxinos.superuser"});
        SUAPP_MAPPING.put(SuBinary.Type.BAIYI_MOBILE_EASYROOT, new String[]{"com.baiyi_mobile.easyroot"});
        SUAPP_MAPPING.put(SuBinary.Type.TENCENT_APPMANAGER, new String[]{"com.tencent.qrom.appmanager"});
        SUAPP_MAPPING.put(SuBinary.Type.SE_SUPERUSER, new String[]{"me.phh.superuser"});
    }

    public SuperUserAppTest(Context context, SuBinaryInfo resultSuBinaryTest) {
        super(context);
        mResultSuBinaryTest = resultSuBinaryTest;
    }

    @Override
    public SuAppInfo test() {
        SuAppInfo result = new SuAppInfo();
        for (Map.Entry<SuBinary.Type, String[]> entry : SUAPP_MAPPING.entrySet()) {
            // Otherwise we show the settings app as superuser app on stock ROMs
            if (mResultSuBinaryTest.getPrimary().getType() != SuBinary.Type.CYANOGENMOD && entry.getKey() == SuBinary.Type.CYANOGENMOD)
                continue;
            for (String pkg : entry.getValue()) {
                try {
                    PackageInfo app = getContext().getPackageManager().getPackageInfo(pkg, PackageManager.GET_UNINSTALLED_PACKAGES);
                    SuApp suApp = new SuApp(app.packageName);
                    suApp.mVersionName = app.versionName;
                    suApp.mVersionCode = app.versionCode;

                    if (app.applicationInfo != null) {
                        suApp.mName = (String) app.applicationInfo.loadLabel(getContext().getPackageManager());
                        suApp.mPath = new File(app.applicationInfo.publicSourceDir);
                        suApp.mSystemApp = (app.applicationInfo.flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;
                    }
                    suApp.mType = entry.getKey();
                    result.getSuperUserApps().add(suApp);
                } catch (PackageManager.NameNotFoundException ignored) {
                }
            }
        }
        for (SuApp suApp : result.getSuperUserApps())
            Logy.d(TAG, suApp.toString());
        return result;
    }
}
