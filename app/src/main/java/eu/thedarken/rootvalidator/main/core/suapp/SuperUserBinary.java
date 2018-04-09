/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.suapp;

import android.support.annotation.Nullable;

import java.util.List;

import eu.darken.rxshell.root.SuBinary;

public class SuperUserBinary extends SuBinary {

    private boolean primary;
    private final String permission;
    private final String owner;
    private final String group;

    public SuperUserBinary(Type type, String path, @Nullable String version, @Nullable String extra, List<String> raw,
                           boolean primary, String permission, String owner, String group) {
        super(type, path, version, extra, raw);

        this.primary = primary;
        this.permission = permission;
        this.owner = owner;
        this.group = group;
    }

    public boolean isPrimary() {
        return primary;
    }

    public String getPermission() {
        return permission;
    }

    public String getOwner() {
        return owner;
    }

    public String getGroup() {
        return group;
    }

}
