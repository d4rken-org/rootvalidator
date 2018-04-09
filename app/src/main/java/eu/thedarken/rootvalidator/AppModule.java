package eu.thedarken.rootvalidator;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import eu.thedarken.rootvalidator.main.core.TestSuite;
import eu.thedarken.rootvalidator.main.core.applets.AppletBinaryTestSuite;
import eu.thedarken.rootvalidator.main.core.root.RootTestSuite;
import eu.thedarken.rootvalidator.main.core.selinux.SELinuxTestSuite;
import eu.thedarken.rootvalidator.main.core.suapp.SuperUserAppTestSuite;

@Module
public class AppModule {
    @Provides
    List<TestSuite> actionModules(
            AppletBinaryTestSuite boxTest,
            RootTestSuite rootTest,
            SuperUserAppTestSuite suAppTest,
            SELinuxTestSuite seLinuxTest
    ) {
        return Arrays.asList(rootTest, suAppTest, boxTest, seLinuxTest);
    }

}
