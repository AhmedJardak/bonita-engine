/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 ******************************************************************************/

package com.bonitasoft.engine.business.application.importer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Arrays;

import org.bonitasoft.engine.commons.exceptions.SObjectCreationException;
import org.bonitasoft.engine.exception.ExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bonitasoft.engine.business.application.ApplicationService;
import com.bonitasoft.engine.business.application.converter.ApplicationContainerConverter;
import com.bonitasoft.engine.business.application.model.SApplication;
import com.bonitasoft.engine.business.application.xml.ApplicationNodeContainer;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationImporterTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ApplicationImportStrategy strategy;

    @Mock
    private ApplicationContainerImporter containerImporter;

    @Mock
    private ApplicationContainerConverter containerConverter;

    @InjectMocks
    private ApplicationImporter applicationImporter;

    @Test
    public void importApplications_should_create_applications_contained_in_xml_file() throws Exception {
        //given
        long createdBy = 5L;
        SApplication app1 = mock(SApplication.class);
        SApplication app2 = mock(SApplication.class);

        ApplicationNodeContainer nodeContainer = mock(ApplicationNodeContainer.class);
        given(containerImporter.importXML("<applications/>".getBytes())).willReturn(nodeContainer);
        given(containerConverter.toSApplications(nodeContainer, createdBy)).willReturn(Arrays.asList(app1, app2));

        //when
        applicationImporter.importApplications("<applications/>".getBytes(), createdBy);

        //then
        verify(applicationService, times(1)).createApplication(app1);
        verify(applicationService, times(1)).createApplication(app2);
        verifyZeroInteractions(strategy);
    }

    @Test
        public void importApplications_should_call_importStrategy_when_application_alredy_exists() throws Exception {
        //given
        long createdBy = 5L;
        SApplication appToBeImported = mock(SApplication.class);
        given(appToBeImported.getToken()).willReturn("application");

        SApplication appInConflict = mock(SApplication.class);

        ApplicationNodeContainer nodeContainer = mock(ApplicationNodeContainer.class);
        given(containerImporter.importXML("<applications/>".getBytes())).willReturn(nodeContainer);
        given(containerConverter.toSApplications(nodeContainer, createdBy)).willReturn(Arrays.asList(appToBeImported));
        given(applicationService.getApplicationByToken("application")).willReturn(appInConflict);

        //when
        applicationImporter.importApplications("<applications/>".getBytes(), createdBy);

        //then
        verify(applicationService, times(1)).createApplication(appToBeImported);
        verify(strategy, times(1)).whenApplicationExists(appInConflict, appToBeImported);
    }

    @Test(expected = ExecutionException.class)
    public void importApplications_should_throw_ExecutionException_when_application_service_throws_exception() throws Exception {
        //given
        long createdBy = 5L;
        SApplication app1 = mock(SApplication.class);

        ApplicationNodeContainer nodeContainer = mock(ApplicationNodeContainer.class);
        given(containerImporter.importXML("<applications/>".getBytes())).willReturn(nodeContainer);
        given(containerConverter.toSApplications(nodeContainer, createdBy)).willReturn(Arrays.asList(app1));

        given(applicationService.createApplication(app1)).willThrow(new SObjectCreationException(""));

        //when
        applicationImporter.importApplications("<applications/>".getBytes(), createdBy);

        //then exception
    }
}
