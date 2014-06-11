/*******************************************************************************
 * Copyright (C) 2011-2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.core.process.instance.model.archive.builder.impl;

import org.bonitasoft.engine.core.process.instance.model.archive.builder.impl.SAProcessInstanceBuilderImpl;
import org.bonitasoft.engine.core.process.instance.model.archive.impl.SAProcessInstanceImpl;

import com.bonitasoft.engine.core.process.instance.model.archive.builder.SAProcessInstanceBuilder;

/**
 * @author Celine Souchet
 */
public class SAProcessInstanceBuilderExt extends SAProcessInstanceBuilderImpl implements SAProcessInstanceBuilder {

    public SAProcessInstanceBuilderExt(final SAProcessInstanceImpl entity) {
        super(entity);
    }

    
}
