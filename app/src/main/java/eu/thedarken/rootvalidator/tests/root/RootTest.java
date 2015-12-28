/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests.root;

import android.content.Context;

import java.io.File;

import eu.thedarken.rootvalidator.tests.ATest;
import eu.thedarken.rootvalidator.tools.Cmd;
import eu.thedarken.rootvalidator.tools.Logy;

public class RootTest extends ATest {
    private static final String TAG = "RV:RootTest";

    public RootTest(Context context) {
        super(context);
    }

    @Override
    public RootInfo test() {
        RootInfo result = new RootInfo();
        Cmd cmd = new Cmd();
        cmd.setRaw(result.getRaw());
        cmd.setRuntimeExec("su");
        cmd.addCommand("echo -RVEOF-");
        cmd.addCommand("id");
        cmd.execute();
        result.mExitCode = cmd.getExitCode();
        if (cmd.getExitCode() == Cmd.OK || cmd.getExitCode() == Cmd.OUT_OF_RANGE) {
            for (String line : cmd.getOutput()) {
                if (line.contains("uid=0")) {
                    Logy.i(TAG, "Root try successfull, we got ROOT :D!");
                    result.mGotRoot = true;
                } else if (line.contains("-RVEOF-")) {
                    result.mSuLaunchesShell = true;
                }
            }
            if (result.mSuLaunchesShell && !result.mGotRoot)
                Logy.i(TAG, "'su' launches a shell, but 'id' seemed to fail.");
        } else if (cmd.getExitCode() == Cmd.COMMAND_NOT_FOUND) {
            // System has no id binary???
            Logy.i(TAG, "IOException, System has no id or echo binary?");
            result.mBinaryIssue = true;
            cmd.clearCommands();
            cmd.addCommand("echo -RVEOF-");
            File testFolder = new File("/cache/rootvalidator.tmp");
            cmd.addCommand("echo test > " + testFolder.getAbsolutePath());
            cmd.execute();

            if (cmd.getExitCode() == Cmd.OK) {
                result.mGotRoot = true;
                result.mSuLaunchesShell = true;
            } else {
                for (String line : cmd.getOutput()) {
                    if (line.contains("-RVEOF-")) {
                        Logy.i(TAG, "'su' launched a shell, but write test was unsuccessful (" + testFolder.getAbsolutePath() + ")");
                        result.mSuLaunchesShell = true;
                    }
                }
            }
        } else if (cmd.getExitCode() == Cmd.PROBLEM) {
            // Unknown error, check if su was denied by su app?
            Logy.i(TAG, "Couldn't launch root shell.");
        }
        Logy.d(TAG, result.toString());
        return result;
    }
}
