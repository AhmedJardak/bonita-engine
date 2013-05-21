/*******************************************************************************
 * Copyright (C) 2009, 2012 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.platform;

import java.text.MessageFormat;

import org.bonitasoft.engine.exception.BonitaException;

/**
 * @author Baptiste Mesta
 */
public class TenantNotActivatedException extends BonitaException {

    private static final long serialVersionUID = -8827675679190216044L;

    public TenantNotActivatedException(final String tenantName) {
        super(MessageFormat.format("the tenant with name ''{0}'' is not activated", tenantName));
    }

    public TenantNotActivatedException(final long tenantId) {
        super(MessageFormat.format("the tenant with id ''{0}'' is not activated", tenantId));
    }
}
