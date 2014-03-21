/*******************************************************************************
 * BonitaSoft is a trademark of BonitaSoft SA.
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api;

import java.io.Serializable;
import java.util.Map;

import org.bonitasoft.engine.api.NoSessionRequired;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.session.APISession;

/**
 * @author Matthieu Chaffotte
 */
public interface LoginAPI extends org.bonitasoft.engine.api.LoginAPI {

    /**
     * Connects the user in order to use API methods of a tenant.
     * 
     * @param tenantId
     *            the tenant identifier
     * @param userName
     *            the user name
     * @param password
     *            the password
     * @return the session to use with other tenant API methods
     * @throws LoginException
     *             occurs when an exception is thrown during the login (userName does not exist, or couple (userName, password) is incorrect)
     * @throws TenantIsPausedException
     *             occurs when we try to login with an other user than the technical user on a tenant that is in maintenance
     * @since 6.0
     */
    @NoSessionRequired
    APISession login(long tenantId, String userName, String password) throws LoginException, TenantIsPausedException;

    /**
     * Connects the user in order to use API methods of a tenant.
     * 
     * @param tenantId
     *            the tenant identifier
     * @param userName
     *            the user name
     * @param password
     *            the password
     * @return the session to use with other tenant API methods
     * @throws LoginException
     *             occurs when an exception is thrown during the login (userName does not exist, or couple (userName, password) is incorrect)
     *             since 6.0
     */
    @NoSessionRequired
    APISession login(long tenantId, Map<String, Serializable> credentials) throws LoginException;

}
