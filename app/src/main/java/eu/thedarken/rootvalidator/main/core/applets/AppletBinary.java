/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.applets;

public class AppletBinary {
    private final String path;
    private final boolean isPrimary;
    private final boolean isExecutable;
    private final String version;
    private final String permission;
    private final String owner;
    private final String group;


    public AppletBinary(String path, boolean isPrimary, String permission, String owner, String group, boolean isExecutable, String version) {
        this.path = path;
        this.isPrimary = isPrimary;
        this.permission = permission;
        this.owner = owner;
        this.group = group;
        this.isExecutable = isExecutable;
        this.version = version;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}