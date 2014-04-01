/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.bpm.parameter;

import org.bonitasoft.engine.exception.NotFoundException;

/**
 * @author Matthieu Chaffotte
 */
public class ParameterNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -5548436489951596184L;

    public ParameterNotFoundException(final Throwable cause) {
        super(cause);
    }

    public ParameterNotFoundException(final long processDefinitionId, final String parameterName) {
        super("the parameter with name " + parameterName + " and process with id " + processDefinitionId + " was not found.");
    }
}
