/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.bonitasoft.engine.api.PlatformLoginAPI;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserCriterion;
import org.bonitasoft.engine.platform.PlatformLoginException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.session.InvalidSessionException;
import org.bonitasoft.engine.session.PlatformSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bonitasoft.engine.api.IdentityAPI;
import com.bonitasoft.engine.api.LoginAPI;
import com.bonitasoft.engine.api.PlatformAPI;
import com.bonitasoft.engine.api.PlatformAPIAccessor;
import com.bonitasoft.engine.api.TenantAPIAccessor;
import com.bonitasoft.engine.platform.TenantActivationException;
import com.bonitasoft.engine.platform.TenantCreator;
import com.bonitasoft.engine.platform.TenantDeactivationException;
import com.bonitasoft.engine.platform.TenantNotFoundException;

/**
 * @author Yanyan Liu
 * @author Celine Souchet
 */
public class TenantTest {

    private final static String userName = "tenant_name";

    private final static String password = "tenant_password";

    private static long tenantId;

    private static final Object LOCK = new Object();

    private APISession apiSession;

    private static PlatformAPI platformAPI;

    private static PlatformLoginAPI platformLoginAPI;

    private static PlatformSession session;

    @BeforeClass
    public static void beforeClass() throws BonitaException {
        platformLoginAPI = PlatformAPIAccessor.getPlatformLoginAPI();
        logAsPlatformAdmin();
        try {
            platformAPI.initializePlatform();
        } catch (final CreationException e) {
            // Platform already created
        }
        platformAPI.startNode();
        createTenant();
    }

    private static void createTenant() throws CreationException, AlreadyExistsException, TenantNotFoundException, TenantActivationException {
        tenantId = platformAPI.createTenant(new TenantCreator("tenant", "tenant", "testIconName", "testIconPath", userName, password));
        platformAPI.activateTenant(tenantId);
        tenantId = platformAPI.getTenantByName("tenant").getId();
    }

    private static void logAsPlatformAdmin() throws PlatformLoginException, BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        session = platformLoginAPI.login("platformAdmin", "platform");
        platformAPI = PlatformAPIAccessor.getPlatformAPI(session);
    }

    @AfterClass
    public static void afterClass() throws BonitaException {
        deleteTenant();
        platformAPI.stopNode();
        platformAPI.cleanPlatform();
        platformLoginAPI.logout(session);
    }

    private static void deleteTenant() throws TenantNotFoundException, TenantDeactivationException, DeletionException {
        platformAPI.deactiveTenant(tenantId);
        platformAPI.deleteTenant(tenantId);
    }

    @Test
    public void singleThreadTenant() throws Exception {
        final LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
        final APISession apiSession = loginAPI.login(tenantId, userName, password);
        final IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);

        identityAPI.createUser("auser1", "bpm");
        final List<User> users = identityAPI.getUsers(0, 5, UserCriterion.USER_NAME_ASC);
        assertEquals(1, users.size());

        identityAPI.deleteUser("auser1");
    }

    @Test
    public void deactivateTenantDeleteSession() throws Exception {
        final LoginAPI loginAPI = TenantAPIAccessor.getLoginAPI();
        final APISession apiSession = loginAPI.login(tenantId, userName, password);
        final IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);
        // will work
        identityAPI.getNumberOfUsers();
        platformAPI.deactiveTenant(tenantId);
        try {
            identityAPI.getNumberOfUsers();
            fail("should not be able to call an api on a deactivated tenant");
        } catch (InvalidSessionException e) {
            platformAPI.activateTenant(tenantId);
        }

    }

    @Test
    public void multiThreadTenant() throws Exception {
        final LoginThread login = new LoginThread();
        final Thread loginThread = new Thread(login);
        final GetUserRequestThread getUser = new GetUserRequestThread(login);
        final Thread getUserThread = new Thread(getUser);
        loginThread.start();
        getUserThread.start();
        synchronized (LOCK) {
            while (!getUser.isDone() && !getUser.isFailed()) {
                try {
                    LOCK.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (getUser.isFailed()) {
            throw new Exception("failed to retrieve user");
        }
        final List<User> users = getUser.getUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        final IdentityAPI identityAPI = TenantAPIAccessor.getIdentityAPI(TenantTest.this.apiSession);
        identityAPI.deleteUser("auser1");
    }

    class LoginThread implements Runnable {

        private boolean failed = false;

        @Override
        public void run() {
            LoginAPI loginAPI;
            synchronized (LOCK) {
                try {
                    loginAPI = TenantAPIAccessor.getLoginAPI();
                    apiSession = loginAPI.login(tenantId, userName, password);
                } catch (final Exception e) {
                    failed = true;
                    throw new RuntimeException(e);
                } finally {
                    LOCK.notifyAll();
                }
            }
        }

        public boolean isFailed() {
            return failed;
        }

    }

    class GetUserRequestThread implements Runnable {

        private boolean done = false;

        private List<User> users;

        private final LoginThread loginThread;

        private boolean failed;

        public GetUserRequestThread(final LoginThread login) {
            loginThread = login;
        }

        @Override
        public void run() {
            IdentityAPI identityAPI;
            synchronized (LOCK) {
                try {
                    while (apiSession == null && !loginThread.isFailed()) {
                        LOCK.wait();
                    }
                    if (loginThread.isFailed()) {
                        failed = true;
                        throw new RuntimeException("login failed");
                    }
                    identityAPI = TenantAPIAccessor.getIdentityAPI(apiSession);
                    identityAPI.createUser("auser1", "bpm");
                    users = identityAPI.getUsers(0, 5, UserCriterion.USER_NAME_ASC);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    done = true;
                    LOCK.notifyAll();
                }
            }

        }

        public boolean isDone() {
            return done;
        }

        public List<User> getUsers() {
            return users;
        }

        public boolean isFailed() {
            return failed;
        }
    }

}
