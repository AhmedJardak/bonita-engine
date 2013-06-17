/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api.impl;

import org.bonitasoft.engine.api.impl.IdentityAPIImpl;
import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.transaction.TransactionExecutor;
import org.bonitasoft.engine.exception.BonitaRuntimeException;
import org.bonitasoft.engine.identity.OrganizationExportException;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;

import com.bonitasoft.engine.api.IdentityAPI;
import com.bonitasoft.engine.identity.xml.ExportOrganization;
import com.bonitasoft.engine.service.TenantServiceAccessor;
import com.bonitasoft.engine.service.impl.LicenseChecker;
import com.bonitasoft.engine.service.impl.ServiceAccessorFactory;
import com.bonitasoft.engine.service.impl.TenantServiceSingleton;
import com.bonitasoft.manager.Features;

/**
 * @author Celine Souchet
 * 
 */
public class IdentityAPIExt extends IdentityAPIImpl implements IdentityAPI {

    @Override
    protected TenantServiceAccessor getTenantAccessor() {
        try {
            final SessionAccessor sessionAccessor = ServiceAccessorFactory.getInstance().createSessionAccessor();
            final long tenantId = sessionAccessor.getTenantId();
            return TenantServiceSingleton.getInstance(tenantId);
        } catch (final Exception e) {
            throw new BonitaRuntimeException(e);
        }
    }

    @Override
    public String exportOrganization() throws OrganizationExportException {
        LicenseChecker.getInstance().checkLicenceAndFeature(Features.WEB_ORGANIZATION_EXCHANGE);

        final TenantServiceAccessor tenantAccessor = getTenantAccessor();
        final TransactionExecutor transactionExecutor = tenantAccessor.getTransactionExecutor();
        final ExportOrganization exportOrganization = new ExportOrganization(tenantAccessor.getXMLWriter(), tenantAccessor.getIdentityService());
        try {
            transactionExecutor.execute(exportOrganization);
            return exportOrganization.getResult();
        } catch (final SBonitaException e) {
            throw new OrganizationExportException(e);
        }
    }
}
