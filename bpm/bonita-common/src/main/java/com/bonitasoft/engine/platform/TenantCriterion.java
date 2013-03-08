/*******************************************************************************
 * Copyright (C) 2009, 2012 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.platform;

/**
 * @author Lu Kai
 * @author Feng Hui
 */
public enum TenantCriterion {

    /**
     * Name ascending order
     */
    NAME_ASC,
    /**
     * Description ascending order
     */
    DESC_ASC,
    /**
     * Creation Date ascending order
     */
    CREATION_ASC,
    /**
     * State ascending order
     */
    STATE_ASC,
    /**
     * Name descending order
     */
    NAME_DESC,
    /**
     * Description descending order
     */
    DESC_DESC,
    /**
     * Creation Date descending order
     */
    CREATION_DESC,
    /**
     * State descending order
     */
    STATE_DESC,

    /**
     * 
     */
    DEFAULT

}
