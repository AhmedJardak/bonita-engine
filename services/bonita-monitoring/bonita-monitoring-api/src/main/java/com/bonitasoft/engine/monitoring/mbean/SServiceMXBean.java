/*******************************************************************************
 * Copyright (C) 2011-2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.monitoring.mbean;

import com.bonitasoft.engine.monitoring.SMonitoringException;

/**
 * @author Christophe Havard
 * @author Matthieu Chaffotte
 */
public interface SServiceMXBean extends BonitaMXBean {

    /**
     * Return the current number of active transaction.
     * 
     * @return
     * @throws SMonitoringException
     */
    long getNumberOfActiveTransactions() throws SMonitoringException;

    /**
     * return the current number of executing jobs
     * 
     * @throws SMonitoringException
     */
    long getNumberOfExecutingJobs() throws SMonitoringException;

}
