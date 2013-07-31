/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.core.reporting;

import java.util.Arrays;

/**
 * Represents the binary content of a report.
 * 
 * @author Emmanuel Duchastenier
 */
public class SReportContentImpl implements SReportContent {

    private long tenantId;

    private long id;

    private byte[] content;

    @Override
    public long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(final long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(final long id) {
        this.id = id;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    public void setContent(final byte[] content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(content);
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (tenantId ^ (tenantId >>> 32));
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SReportContentImpl other = (SReportContentImpl) obj;
        if (!Arrays.equals(content, other.content))
            return false;
        if (id != other.id)
            return false;
        if (tenantId != other.tenantId)
            return false;
        return true;
    }

    @Override
    public String getDiscriminator() {
        return SReportContentImpl.class.getName();
    }
}
