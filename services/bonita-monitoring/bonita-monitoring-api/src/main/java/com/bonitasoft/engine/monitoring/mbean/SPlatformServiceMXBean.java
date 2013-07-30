/*******************************************************************************
 * Copyright (C) 2011-2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.monitoring.mbean;

/**
 * @author Elias Ricken de Medeiros
 * @author Matthieu Chaffotte
 */
public interface SPlatformServiceMXBean extends BonitaMXBean {

    /**
     * Return true if the scheduler service is started, false if it is stopped.
     */
    boolean isSchedulerStarted();

    /**
     * Return the current number of active transactions.
     * 
     * @return the current number of active transactions.
     */
    long getNumberOfActiveTransactions();

}
