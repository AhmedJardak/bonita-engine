/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.business.application;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public interface SApplicationBuilderFactory {

    SApplicationBuilder createNewInstance(String name, String version, String path, final long createdBy);

    String getIdKey();

    String getNameKey();

    String getVersionKey();

    String getPathKey();

    String getDescriptionKey();

    String getIconPathKey();

    String getCreationDateKey();

    String getCreatedByKey();

    String getLastUpdatedDateKey();

    String getUpdatedByKey();

    String getStateKey();

}
