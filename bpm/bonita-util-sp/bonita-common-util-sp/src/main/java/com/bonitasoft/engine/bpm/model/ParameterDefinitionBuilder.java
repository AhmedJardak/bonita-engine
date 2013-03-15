/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.bpm.model;

import org.bonitasoft.engine.bpm.model.impl.DesignProcessDefinitionImpl;
import org.bonitasoft.engine.bpm.model.impl.ParameterDefinitionImpl;

/**
 * @author Matthieu Chaffotte
 */
public class ParameterDefinitionBuilder extends ProcessBuilder {

    private final ParameterDefinitionImpl parameter;

    ParameterDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final DesignProcessDefinitionImpl process, final String parameterName,
            final String type) {
        super(process, processDefinitionBuilder);
        parameter = new ParameterDefinitionImpl(parameterName, type);
        process.addParameter(parameter);
    }

    public ParameterDefinitionBuilder addDescription(final String description) {
        parameter.setDescription(description);
        return this;
    }

}
