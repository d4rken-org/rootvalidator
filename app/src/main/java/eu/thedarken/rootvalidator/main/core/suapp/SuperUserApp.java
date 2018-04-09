/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.suapp;

import android.support.annotation.Nullable;

import eu.darken.rxshell.root.SuApp;
import eu.darken.rxshell.root.SuBinary;

public class SuperUserApp extends SuApp {

    @Nullable private final String name;
    private final boolean systemApp;
    private final boolean isPrimary;

    public SuperUserApp(SuBinary.Type type,
                        @Nullable String pkg,
                        @Nullable String versionName,
                        @Nullable Integer versionCode,
                        @Nullable String apkPath,
                        @Nullable String name,
                        boolean systemApp,
                        boolean isPrimary

    ) {
        super(type, pkg, versionName, versionCode, apkPath);
        this.name = name;
        this.systemApp = systemApp;
        this.isPrimary = isPrimary;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    public boolean isPrimary() {
        return isPrimary;
    }
}
