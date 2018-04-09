/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.root;

import android.content.Context;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import eu.darken.rxshell.cmd.Cmd;
import eu.darken.rxshell.cmd.RxCmdShell;
import eu.darken.rxshell.root.Root;
import eu.thedarken.rootvalidator.main.core.TestResult;
import eu.thedarken.rootvalidator.main.core.TestSuite;
import eu.thedarken.rootvalidator.tools.StringUtils;
import io.reactivex.Single;

public class RootTestSuite extends TestSuite {

    @Inject
    public RootTestSuite(Context context) {
        super(context);
    }

    @Override
    public Single<List<TestResult>> test() {
        return Single.create(emitter -> {
            final RootResult.Builder resultBuilder = new RootResult.Builder();

            final Root root = new Root.Builder().build().blockingGet();
            final Root.State state = root.getState();
            resultBuilder.state(state);

            if (state != Root.State.ROOTED) {
                {
                    final Cmd.Result result = Cmd.builder("id").execute(RxCmdShell.builder().root(true).build());
                    resultBuilder.launchIssue(result.getExitCode() != Cmd.ExitCode.OK && result.getOutput().isEmpty());
                    resultBuilder.idIssue(!StringUtils.join(result.merge()).contains("uid=0"));
                }
                {
                    final File testFile = new File("/cache/rootvalidator.tmp");
                    Cmd.builder("echo test > " + testFile.getPath(), "chmod 444 " + testFile.getPath()).execute(RxCmdShell.builder().root(true).build());
                    resultBuilder.exitCodeIssue(testFile.exists());
                    if (testFile.exists()) {
                        Cmd.builder("rm  " + testFile.getPath()).execute(RxCmdShell.builder().root(true).build());
                    }
                }
            }
            emitter.onSuccess(Collections.singletonList(resultBuilder.build()));
        });

    }
}
