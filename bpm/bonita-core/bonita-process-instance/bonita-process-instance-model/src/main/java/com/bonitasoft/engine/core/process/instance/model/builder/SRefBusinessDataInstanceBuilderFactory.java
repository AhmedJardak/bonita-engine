/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.core.process.instance.model.builder;

/**
 * @author Matthieu Chaffotte
 */
public interface SRefBusinessDataInstanceBuilderFactory {

    SRefBusinessDataInstanceBuilder createNewInstance(String name, long processInstanceId, long dataId, String dataClassName);

    String getNameKey();

    String getProcessInstanceIdKey();

    String getDataIdKey();

    String getDataClassNameKey();

}
