/**
 * Copyright (C) 2011-2014 BonitaSoft S.A.
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
package org.bonitasoft.engine.api;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.bonitasoft.engine.api.internal.ServerAPI;
import org.bonitasoft.engine.api.internal.ServerWrappedException;
import org.bonitasoft.engine.exception.ServerAPIException;

/**
 * @author Matthieu Chaffotte
 * @author Aurelien Pupier
 */
public class EJB3ServerAPI implements ServerAPI {

    private static final long serialVersionUID = 1L;

    private final ServerAPI remoteServAPI;

    public EJB3ServerAPI(final Map<String, String> parameters) throws ServerAPIException {
        try {
            remoteServAPI = lookup("serverAPI", new Hashtable<String, String>(parameters));
        } catch (final NamingException e) {
            throw new ServerAPIException(e);
        }
    }

    private ServerAPI lookup(final String name, final Hashtable<String, String> environment) throws NamingException {
        InitialContext initialContext = null;
        if (environment != null) {
            initialContext = new InitialContext(environment);
        } else {
            initialContext = new InitialContext();
        }
        return (ServerAPI) initialContext.lookup(name);
    }

    @Override
    public Object invokeMethod(final Map<String, Serializable> options, final String apiInterfaceName, final String methodName,
    	final List<String> classNameParameters, final Object[] parametersValues) throws ServerWrappedException, RemoteException {
    	return remoteServAPI.invokeMethod(options, apiInterfaceName, methodName, classNameParameters, parametersValues);
    }

}
