/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.core.page;

import org.bonitasoft.engine.persistence.PersistentObject;

/**
 * @author Emmanuel Duchastenier
 */
public interface SPageContent extends PersistentObject {

    public long getTenantId();

    /**
     * @return the binary content of the report
     */
    public byte[] getContent();

}
