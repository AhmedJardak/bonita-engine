/*******************************************************************************
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.PlatformLoginAPI;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.PlatformSession;
import org.bonitasoft.engine.test.ClientEventUtil;
import org.bonitasoft.engine.test.runner.BonitaSuiteRunner;
import org.bonitasoft.engine.test.runner.BonitaSuiteRunner.Initializer;
import org.bonitasoft.engine.util.APITypeManager;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import com.bonitasoft.engine.api.PlatformAPI;
import com.bonitasoft.engine.api.PlatformAPIAccessor;

@RunWith(BonitaSuiteRunner.class)
@SuiteClasses({ ClusterTests.class })
@Initializer(ClusteredTestSuiteIT.class)
public class ClusteredTestSuiteIT {

    public static void beforeAll() throws Exception {
        System.err.println("=================== ClusteredTestSuiteIT.beforeClass()");
        changeToNode2();
        final PlatformLoginAPI platformLoginAPI = PlatformAPIAccessor.getPlatformLoginAPI();
        final PlatformSession platformSession = platformLoginAPI.login("platformAdmin", "platform");
        final PlatformAPI platformAPI = PlatformAPIAccessor.getPlatformAPI(platformSession);
        BPMTestSPUtil.setDefaultTenantId(platformAPI.getDefaultTenant().getId());
        platformLoginAPI.logout(platformSession);
        final APISession loginDefaultTenant = BPMTestSPUtil.loginOnDefaultTenantWithDefaultTechnicalUser();
        ClientEventUtil.deployCommand(loginDefaultTenant);
        BPMTestSPUtil.logoutOnTenant();
        changeToNode1();
    }

    public static void afterAll() throws Exception {
        changeToNode2();
        final APISession loginDefaultTenant = BPMTestSPUtil.loginOnDefaultTenantWithDefaultTechnicalUser();
        ClientEventUtil.undeployCommand(loginDefaultTenant);
        BPMTestSPUtil.logoutOnTenant();
        final PlatformLoginAPI platformLoginAPI = PlatformAPIAccessor.getPlatformLoginAPI();
        final PlatformSession platformSession = platformLoginAPI.login("platformAdmin", "platform");
        final PlatformAPI platformAPI = PlatformAPIAccessor.getPlatformAPI(platformSession);
        platformAPI.stopNode();
        changeToNode1();
        platformAPI.stopNode();
        platformLoginAPI.logout(platformSession);
        System.err.println("=================== ClusteredTestSuiteIT.afterClass()");
        BPMTestSPUtil.destroyPlatformAndTenants();
        new APITestSPUtil().deletePlatformStructure();
    }

    protected static void changeToNode1() {
        setConnectionPort("8186");
    }

    protected static void changeToNode2() {
        setConnectionPort("8187");
    }

    private static void setConnectionPort(final String port) {
        final Map<String, String> parameters = new HashMap<String, String>(2);
        parameters.put("server.url", "http://localhost:" + port);
        parameters.put("application.name", "bonita");
        APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, parameters);
    }

}
