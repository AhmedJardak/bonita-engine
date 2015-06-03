/*******************************************************************************
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.PrintTestsStatusRule;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.session.PlatformSession;
import org.bonitasoft.engine.test.runner.BonitaTestRunner;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonitasoft.engine.api.PlatformAPI;
import com.bonitasoft.engine.api.PlatformAPIAccessor;
import com.bonitasoft.engine.platform.Tenant;

@RunWith(BonitaTestRunner.class)
public abstract class CommonAPISPIT extends APITestSPUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonAPISPIT.class);

    @Rule
    public TestRule testWatcher = new PrintTestsStatusRule(LOGGER) {

        @Override
        public List<String> clean() throws Exception {
            return CommonAPISPIT.this.clean();
        }
    };

    /**
     * @return the warning message of unclean elements
     * @throws BonitaException
     */
    private List<String> clean() throws BonitaException {
        final List<String> messages = new ArrayList<>();
        final PlatformSession platformSession = BPMTestSPUtil.loginOnPlatform();
        final PlatformAPI platformAPI = PlatformAPIAccessor.getPlatformAPI(platformSession);
        final List<Tenant> tenants = platformAPI.searchTenants(new SearchOptionsBuilder(0, 100).done()).getResult();
        BPMTestSPUtil.logoutOnPlatform(platformSession);

        for (final Tenant tenant : tenants) {
            loginOnTenantWithTechnicalLogger(tenant.getId());
            if (getTenantAdministrationAPI().isPaused()) {
                messages.add("Tenant was in paused state");
                getTenantAdministrationAPI().resume();
            }
            messages.addAll(checkNoCommands());
            messages.addAll(checkNoFlowNodes());
            messages.addAll(checkNoArchivedFlowNodes());
            messages.addAll(checkNoComments());
            messages.addAll(checkNoArchivedComments());
            messages.addAll(checkNoWaitingEvent());
            messages.addAll(checkNoProcessIntances());
            messages.addAll(checkNoArchivedProcessIntances());
            messages.addAll(checkNoProcessDefinitions());
            messages.addAll(checkNoCategories());
            messages.addAll(checkNoUsers());
            messages.addAll(checkNoGroups());
            messages.addAll(checkNoRoles());
            messages.addAll(checkNoSupervisors());
            messages.addAll(checkNoReports());
            logoutOnTenant();
        }
        return messages;
    }

}
