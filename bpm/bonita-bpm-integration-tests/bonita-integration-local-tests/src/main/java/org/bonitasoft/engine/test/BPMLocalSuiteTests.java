package org.bonitasoft.engine.test;

import org.bonitasoft.engine.BonitaSuiteRunner;
import org.bonitasoft.engine.BonitaSuiteRunner.Initializer;
import org.bonitasoft.engine.TestsInitializer;
import org.bonitasoft.engine.event.LocalTimerEventTest;
import org.bonitasoft.engine.job.JobExecutionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(BonitaSuiteRunner.class)
@SuiteClasses({
        BPMLocalTest.class,
        ConnectorExecutionsTestsLocal.class,
        ProcessWithExpressionTestLocal.class,
        ProcessArchiveTest.class,
        LocalTimerEventTest.class,
        DataInstanceIntegrationLocalTest.class,
        GatewayExecutionLocalTest.class,
        JobExecutionTest.class
})
@Initializer(TestsInitializer.class)
public class BPMLocalSuiteTests {

}
