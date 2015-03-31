package eu.thedarken.rootvalidator.tests.selinux;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import eu.thedarken.rootvalidator.tests.ATest;
import eu.thedarken.rootvalidator.tools.Cmd;
import eu.thedarken.rootvalidator.tools.Logy;

/**
 * Created by darken on 14.02.2015.
 */
public class SELinuxTest extends ATest {
    private final static String SELINUX_GETENFORCE_DISABLED = "Disabled";
    private final static String SELINUX_GETENFORCE_PERMISSIVE = "Permissive";
    private final static String SELINUX_GETENFORCE_ENFORCING = "Enforcing";
    private static final String TAG = "RV:SELinuxTest";

    public SELinuxTest(Context context) {
        super(context);
    }

    @Override
    public SELinuxInfo test() {
        SELinuxInfo result = new SELinuxInfo();
        // First known firmware with SELinux built-in was a 4.2 (17) leak
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            if (result.getState() == SELinuxInfo.State.UNKNOWN) {
                // Detect enforcing through sysfs, not always present
                File f = new File("/sys/fs/selinux/enforce");
                if (f.exists()) {
                    try {
                        InputStream is = new FileInputStream(f.getAbsoluteFile());
                        try {
                            int value = is.read();
                            result.getRaw().add("read:" + f.getAbsolutePath() + " == " + value);
                            if (value == '1') {
                                result.setState(SELinuxInfo.State.ENFORCING);
                            }
                        } finally {
                            is.close();
                        }
                    } catch (Exception e) {
                        result.getRaw().add("Couldn't read " + f.getAbsolutePath());
                    }
                } else {
                    result.getRaw().add("Does not exist: " + f.getAbsolutePath());
                }
            }
            if (result.getState() == SELinuxInfo.State.UNKNOWN) {
                Cmd cmd = new Cmd();
                cmd.setRaw(result.getRaw());
                cmd.addCommand("getenforce");
                cmd.execute();
                if (cmd.getExitCode() == Cmd.OK) {
                    for (String line : cmd.getOutput()) {
                        if (line.contains(SELINUX_GETENFORCE_DISABLED)) {
                            result.setState(SELinuxInfo.State.DISABLED);
                            break;
                        } else if (line.contains(SELINUX_GETENFORCE_PERMISSIVE)) {
                            result.setState(SELinuxInfo.State.PERMISSIVE);
                            break;
                        } else if (line.contains(SELINUX_GETENFORCE_ENFORCING)) {
                            result.setState(SELinuxInfo.State.ENFORCING);
                            break;
                        }
                    }
                }
            }
        }
        Logy.d(TAG, result.toString());
        return result;
    }
}
