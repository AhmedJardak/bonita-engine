/*******************************************************************************
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine;

import com.bonitasoft.engine.event.SPTimerBoundaryEventTest;
import com.bonitasoft.engine.identity.SPIdentityTests;
import com.bonitasoft.engine.platform.SPPlatformLoginTest;
import com.bonitasoft.engine.platform.SPPlatformTest;
import com.bonitasoft.engine.platform.SPProcessManagementTest;
import com.bonitasoft.engine.tenant.TenantMaintenanceIT;
import org.bonitasoft.engine.event.InterruptingTimerBoundaryEventIT;
import org.bonitasoft.engine.event.MessageBoundaryEventIT;
import org.bonitasoft.engine.event.MessageEventIT;
import org.bonitasoft.engine.event.MessageEventSubProcessIT;
import org.bonitasoft.engine.event.NonInterruptingTimerBoundaryEventIT;
import org.bonitasoft.engine.event.TimerBoundaryEventIT;
import org.bonitasoft.engine.event.TimerEventIT;
import org.bonitasoft.engine.event.TimerEventSubProcessIT;
import org.bonitasoft.engine.platform.PlatformIT;
import org.bonitasoft.engine.platform.PlatformLoginIT;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TestShadesSP.class,
        // Specific slow test suites below:
        NonInterruptingTimerBoundaryEventIT.class,
        InterruptingTimerBoundaryEventIT.class,
        TimerBoundaryEventIT.class,
        TimerEventIT.class,
        MessageEventIT.class,
        MessageBoundaryEventIT.class,
        TimerEventSubProcessIT.class,
        MessageEventSubProcessIT.class,
        PlatformLoginIT.class,
        PlatformIT.class,

        // SP specific slow test suites:
        SPPlatformLoginTest.class,
        SPPlatformTest.class,
        SPTimerBoundaryEventTest.class,
        SPIdentityTests.class,
        TenantMaintenanceIT.class,
        SPProcessManagementTest.class,
        LocalIntegrationTestsSP.class,
})
public class SlowExecutionLocalIntegrationTestsSP {

}
