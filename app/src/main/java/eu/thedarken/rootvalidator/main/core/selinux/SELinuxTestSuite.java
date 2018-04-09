/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.main.core.selinux;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import eu.darken.rxshell.root.SELinux;
import eu.thedarken.rootvalidator.main.core.TestResult;
import eu.thedarken.rootvalidator.main.core.TestSuite;
import io.reactivex.Single;

public class SELinuxTestSuite extends TestSuite {
    @Inject
    public SELinuxTestSuite(Context context) {
        super(context);
    }

    @Override
    public Single<List<TestResult>> test() {
        return Single.create(emitter -> {
            final SELinuxResult.Builder resultBuilder = new SELinuxResult.Builder();
            final SELinux seLinux = new SELinux.Builder().build().blockingGet();
            resultBuilder.state(seLinux.getState());
            emitter.onSuccess(Collections.singletonList(resultBuilder.build()));
        });
    }
}
