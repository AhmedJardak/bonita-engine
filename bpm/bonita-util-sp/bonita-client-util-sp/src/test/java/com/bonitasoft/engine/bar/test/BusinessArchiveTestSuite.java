/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.bar.test;

import org.bonitasoft.engine.bar.test.BusinessArchiveTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Emmanuel Duchastenier
 */
@RunWith(Suite.class)
@SuiteClasses({ BusinessArchiveTests.class, com.bonitasoft.engine.bar.test.BusinessArchiveTests.class })
public class BusinessArchiveTestSuite {

}
