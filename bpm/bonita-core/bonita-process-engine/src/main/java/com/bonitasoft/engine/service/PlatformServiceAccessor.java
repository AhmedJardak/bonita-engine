/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.service;

import com.bonitasoft.engine.search.SearchPlatformEntitiesDescriptor;

/**
 * @author Matthieu Chaffotte
 * @author Elias Ricken de Medeiros
 * @author Zhao Na
 */
public interface PlatformServiceAccessor extends org.bonitasoft.engine.service.PlatformServiceAccessor {

    SearchPlatformEntitiesDescriptor getSearchPlatformEntitiesDescriptor();

    @Override
    TenantServiceAccessor getTenantServiceAccessor(long tenantId);

}
