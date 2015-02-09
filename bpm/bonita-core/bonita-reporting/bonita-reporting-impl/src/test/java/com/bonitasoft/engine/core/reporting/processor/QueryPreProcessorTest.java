/*******************************************************************************
 * Copyright (C) 2009, 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.core.reporting.processor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class QueryPreProcessorTest {

    private QueryPreProcessor queryPreProcessor;

    @Before
    public void setUp() {
        queryPreProcessor = new QueryPreProcessor();
    }

    @Test
    public void should_replace_boolean_value_for_oracle() {
        final String queryString = "SELECT * FROM TASK WHERE MYBOOLEAN = FALSE";
        final String expectedQueryString = "SELECT * FROM TASK WHERE MYBOOLEAN = 0";

        final String processedQuery = queryPreProcessor.preProcessFor(Vendor.ORACLE, queryString);

        assertThat(processedQuery).isEqualTo(expectedQueryString);
    }

    @Test
    public void should_replace_boolean_value_for_sqlserver() {
        final String queryString = "SELECT * FROM TASK WHERE MYBOOLEAN =  false";
        final String expectedQueryString = "SELECT * FROM TASK WHERE MYBOOLEAN = 0";

        final String processedQuery = queryPreProcessor.preProcessFor(Vendor.SQLSERVER, queryString);

        assertThat(processedQuery).isEqualTo(expectedQueryString);
    }

    @Test
    public void should_do_nothing_for_other_databases_vendors() {
        final String expectedQueryString = "SELECT * FROM TASK WHERE MYBOOLEAN = FALSE";

        final String processedQuery = queryPreProcessor.preProcessFor(Vendor.OTHER, expectedQueryString);

        assertThat(processedQuery).isEqualTo(expectedQueryString);
    }

}
