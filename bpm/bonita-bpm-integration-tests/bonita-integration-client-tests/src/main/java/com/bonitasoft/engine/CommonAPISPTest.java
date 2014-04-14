/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine;

import java.util.ArrayList;
import java.util.List;

import org.bonitasoft.engine.BonitaSuiteRunner.Initializer;
import org.bonitasoft.engine.BonitaTestRunner;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.BonitaRuntimeException;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.session.PlatformSession;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonitasoft.engine.api.PlatformAPI;
import com.bonitasoft.engine.api.PlatformAPIAccessor;
import com.bonitasoft.engine.platform.Tenant;

@RunWith(BonitaTestRunner.class)
@Initializer(TestsInitializerSP.class)
public abstract class CommonAPISPTest extends APITestSPUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonAPISPTest.class);

    @Rule
    public TestRule testWatcher = new TestWatcher() {

        @Override
        public void starting(final Description d) {
            LOGGER.info("Starting test: " + d.getClassName() + "." + d.getMethodName());
        }

        @Override
        public void failed(final Throwable cause, final Description d) {
            LOGGER.error("Failed test: " + d.getClassName() + "." + d.getMethodName());
            try {
                clean();
            } catch (final Exception be) {
                LOGGER.error("Unable to clean db", be);
            } finally {
                LOGGER.info("-----------------------------------------------------------------------------------------------");
            }
        }

        @Override
        public void succeeded(final Description d) {
            List<String> clean = null;
            try {
                clean = clean();
            } catch (final BonitaException e) {
                throw new BonitaRuntimeException(e);
            }
            LOGGER.info("Succeeded test: " + d.getClassName() + "." + d.getMethodName());
            LOGGER.info("-----------------------------------------------------------------------------------------------");
            if (!clean.isEmpty()) {
                throw new BonitaRuntimeException(clean.toString());
            }
        }
    };

    /**
     * FIXME: clean actors!
     * 
     * @return
     * @throws BonitaException
     */
    private List<String> clean() throws BonitaException {
        final List<String> messages = new ArrayList<String>();
        final PlatformSession platformSession = SPBPMTestUtil.loginPlatform();
        final PlatformAPI platformAPI = PlatformAPIAccessor.getPlatformAPI(platformSession);
        final List<Tenant> tenants = platformAPI.searchTenants(new SearchOptionsBuilder(0, 100).done()).getResult();
        SPBPMTestUtil.logoutPlatform(platformSession);

        for (final Tenant tenant : tenants) {
            login(tenant.getId());
            messages.addAll(checkNoCommands());
            messages.addAll(checkNoUsers());
            messages.addAll(checkNoGroups());
            messages.addAll(checkNoRoles());
            messages.addAll(checkNoProcessDefinitions());
            messages.addAll(checkNoProcessIntances());
            messages.addAll(checkNoArchivedProcessIntances());
            messages.addAll(checkNoFlowNodes());
            messages.addAll(checkNoArchivedFlowNodes());
            messages.addAll(checkNoCategories());
            messages.addAll(checkNoComments());
            messages.addAll(checkNoArchivedComments());
            messages.addAll(checkNoBreakpoints());
            messages.addAll(checkNoReports());
            messages.addAll(checkNoActiveTransactions());
            // FIXME : Uncomment when fix bug : BS-7206
            // messages.addAll(checkNoDataMappings());
            logout();
        }
        return messages;
    }

}
