/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.EJB2ServerAPI;
import org.bonitasoft.engine.api.EJB3ServerAPI;
import org.bonitasoft.engine.api.HTTPServerAPI;
import org.bonitasoft.engine.api.impl.ClientInterceptor;
import org.bonitasoft.engine.api.impl.LocalServerAPIFactory;
import org.bonitasoft.engine.api.internal.ServerAPI;
import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.ServerAPIException;
import org.bonitasoft.engine.exception.UnknownAPITypeException;
import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.engine.util.APITypeManager;

/**
 * <b>Accessor class that retrieve APIs</b>
 * <p>
 * <ul>
 * <li>{@link ProcessAPI}</li>
 * <li>{@link CommandAPI}</li>
 * <li>{@link IdentityAPI}</li>
 * <li>{@link MonitoringAPI}</li>
 * <li>{@link LoginAPI}</li>
 * <li>{@link ProfileAPI}</li>
 * <li>{@link ReportingAPI}</li>
 * <li>{@link LogAPI}</li>
 * <li>{@link ThemeAPI}</li>
 * </ul>
 * 
 * @author Matthieu Chaffotte
 * @author Celine Souchet
 */
public final class TenantAPIAccessor {

    private static ServerAPI getServerAPI() throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        final ApiAccessType apiType = APITypeManager.getAPIType();
        Map<String, String> parameters = null;
        switch (apiType) {
            case LOCAL:
                return LocalServerAPIFactory.getServerAPI();
            case EJB3:
                parameters = APITypeManager.getAPITypeParameters();
                return new EJB3ServerAPI(parameters);
            case EJB2:
                parameters = APITypeManager.getAPITypeParameters();
                return new EJB2ServerAPI(parameters);
            case HTTP:
                parameters = APITypeManager.getAPITypeParameters();
                return new HTTPServerAPI(parameters);
            default:
                throw new UnknownAPITypeException("Unsupported API Type: " + apiType);
        }
    }

    public static void refresh() {
        APITypeManager.refresh();
    }

    private static <T> T getAPI(final Class<T> clazz, final APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        final ServerAPI serverAPI = getServerAPI();
        final ClientInterceptor clientInterceptor = new ClientInterceptor(clazz.getName(), serverAPI, session);
        return (T) Proxy.newProxyInstance(APIAccessor.class.getClassLoader(), new Class[] { clazz }, clientInterceptor);
    }

    public static LoginAPI getLoginAPI() throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        final ServerAPI serverAPI = getServerAPI();
        final ClientInterceptor interceptor = new ClientInterceptor(LoginAPI.class.getName(), serverAPI);
        return (LoginAPI) Proxy.newProxyInstance(APIAccessor.class.getClassLoader(), new Class[] { LoginAPI.class }, interceptor);
    }

    public static IdentityAPI getIdentityAPI(final APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        return getAPI(IdentityAPI.class, session);
    }

    public static ProcessAPI getProcessAPI(final APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        return getAPI(ProcessAPI.class, session);
    }

    public static MonitoringAPI getMonitoringAPI(final APISession session) throws BonitaHomeNotSetException, UnknownAPITypeException, ServerAPIException {
        return getAPI(MonitoringAPI.class, session);
    }

    public static PlatformMonitoringAPI getPlatformMonitoringAPI(final APISession session) throws BonitaHomeNotSetException, UnknownAPITypeException,
            ServerAPIException {
        return getAPI(PlatformMonitoringAPI.class, session);
    }

    public static LogAPI getLogAPI(final APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        return getAPI(LogAPI.class, session);
    }

    public static ThemeAPI getThemeAPI(final APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        return getAPI(ThemeAPI.class, session);
    }

    public static CommandAPI getCommandAPI(final APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        return getAPI(CommandAPI.class, session);
    }

    public static ProfileAPI getProfileAPI(final APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        return getAPI(ProfileAPI.class, session);
    }

    public static ReportingAPI getReportingAPI(final APISession session) throws BonitaHomeNotSetException, ServerAPIException, UnknownAPITypeException {
        return getAPI(ReportingAPI.class, session);
    }

    public static TenantManagementAPI getTenantManagementAPI(final APISession session) throws BonitaHomeNotSetException, ServerAPIException,
            UnknownAPITypeException {
        return getAPI(TenantManagementAPI.class, session);
    }

}
