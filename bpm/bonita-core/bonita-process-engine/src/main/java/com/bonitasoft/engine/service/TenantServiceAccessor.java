/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.service;

import org.bonitasoft.engine.parameter.ParameterService;

import com.bonitasoft.engine.core.process.instance.api.BreakpointService;
import com.bonitasoft.engine.core.process.instance.model.builder.BPMInstanceBuilders;
import com.bonitasoft.engine.search.descriptor.SearchEntitiesDescriptor;

/**
 * @author Matthieu Chaffotte
 */
public interface TenantServiceAccessor extends org.bonitasoft.engine.service.TenantServiceAccessor {

    ParameterService getParameterService();

    BreakpointService getBreakpointService();

    @Override
    SearchEntitiesDescriptor getSearchEntitiesDescriptor();

    @Override
    BPMInstanceBuilders getBPMInstanceBuilders();

}
