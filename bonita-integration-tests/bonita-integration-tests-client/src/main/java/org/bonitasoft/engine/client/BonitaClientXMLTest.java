/**
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.client;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.PlatformAPIAccessor;
import org.bonitasoft.engine.api.TenantAPIAccessor;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.home.BonitaHome;
import org.bonitasoft.engine.util.APITypeManager;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;

/**
 * @author Elias Ricken de Medeiros
 */
public class BonitaClientXMLTest {

    private static final String BONITA_HOME_CLIENT_INVALID_API_TYPE = "build/bonita_home_client_invalidAPIType";

    private static final String BONITA_HOME_CLIENT_EJB3 = "build/bonita_home_client_EJB3";

    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();

    @After
    public void tearDown() {
        TenantAPIAccessor.refresh();
    }

    @Test
    public void testGetAPIType() throws Exception {

        ApiAccessType apiType = APITypeManager.getAPIType();
        assertEquals(ApiAccessType.LOCAL, apiType);

        TenantAPIAccessor.refresh();
        System.setProperty(BonitaHome.BONITA_HOME, BONITA_HOME_CLIENT_EJB3);
        apiType = APITypeManager.getAPIType();
        assertEquals(ApiAccessType.EJB3, apiType);
    }

    @Test
    public void testGetAPITypeParameters() throws Exception {
        Map<String, String> parameters = APITypeManager.getAPITypeParameters();
        final Map<String, String> expectedParameters = new HashMap<>();
        assertEquals(expectedParameters, parameters);

        TenantAPIAccessor.refresh();
        System.setProperty(BonitaHome.BONITA_HOME, BONITA_HOME_CLIENT_EJB3);
        parameters = APITypeManager.getAPITypeParameters();
        expectedParameters.put("java.naming.factory.url.pkgs", "org.jboss.ejb.client.naming");
        assertEquals(expectedParameters, parameters);
    }

    @Test(expected = UnknownAPITypeException.class)
    public void testCannotUseAnInvalidAPITypePlatForm() throws Exception {
        System.setProperty(BonitaHome.BONITA_HOME, BONITA_HOME_CLIENT_INVALID_API_TYPE);
        PlatformAPIAccessor.getPlatformLoginAPI();
    }

    @Test(expected = UnknownAPITypeException.class)
    public void testCannotUseAnInvalidAPITypeTenants() throws Exception {
        System.setProperty(BonitaHome.BONITA_HOME, BONITA_HOME_CLIENT_INVALID_API_TYPE);
        TenantAPIAccessor.getLoginAPI();
    }

}
