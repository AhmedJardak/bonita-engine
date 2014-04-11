/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.bdm.client;

import java.io.File;

import com.bonitasoft.engine.bdm.AbstractBDMCodeGenerator;
import com.bonitasoft.engine.bdm.AbstractBDMJarBuilder;
import com.bonitasoft.engine.bdm.BusinessObjectModel;
import com.bonitasoft.engine.compiler.JDTCompiler;

/**
 * @author Romain Bioteau
 */
public class ClientBDMJarBuilder extends AbstractBDMJarBuilder {

    public ClientBDMJarBuilder(final JDTCompiler compiler, final String dependencyPath) {
        super(compiler, dependencyPath);
    }

    @Override
    protected AbstractBDMCodeGenerator getBDMCodeGenerator(final BusinessObjectModel bom) {
        return new ClientBDMCodeGenerator(bom);
    }

    @Override
    protected void addPersistenceFile(final File directory, final BusinessObjectModel bom) {
        // DO NOTHING
    }

    @Override
    protected void addBOMFile(final File directory, final BusinessObjectModel bom) {
        // DO NOTHING
    }

}
