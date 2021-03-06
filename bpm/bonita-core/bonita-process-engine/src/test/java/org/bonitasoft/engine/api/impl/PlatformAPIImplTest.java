/**
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.api.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.home.BonitaHomeServer;
import org.bonitasoft.engine.persistence.QueryOptions;
import org.bonitasoft.engine.platform.PlatformService;
import org.bonitasoft.engine.platform.model.STenant;
import org.bonitasoft.engine.platform.model.impl.STenantImpl;
import org.bonitasoft.engine.scheduler.SchedulerService;
import org.bonitasoft.engine.scheduler.exception.SSchedulerException;
import org.bonitasoft.engine.service.PlatformServiceAccessor;
import org.bonitasoft.engine.service.TenantServiceAccessor;
import org.bonitasoft.engine.session.SessionService;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;
import org.bonitasoft.engine.transaction.BonitaTransactionSynchronization;
import org.bonitasoft.engine.transaction.TransactionService;
import org.bonitasoft.engine.transaction.TransactionState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PlatformAPIImplTest {

    public static final long TENANT_ID = 56423L;
    private final List<STenant> tenants = Collections.singletonList(mock(STenant.class));
    @Mock
    private PlatformServiceAccessor platformServiceAccessor;
    @Mock
    private SchedulerService schedulerService;
    @Mock
    private SessionService sessionService;
    @Mock
    private SessionAccessor sessionAccessor;
    @Mock
    private NodeConfiguration platformConfiguration;
    @Mock
    private TenantServiceAccessor tenantServiceAccessor;
    @Mock
    private TenantConfiguration tenantConfiguration;
    @Mock
    private BonitaHomeServer bonitaHomeServer;
    @Mock
    private PlatformService platformService;
    @Mock
    private STenant sTenant;
    private TransactionService transactionService = new MockedTransactionService();
    @Spy
    @InjectMocks
    private PlatformAPIImpl platformAPI;

    @Before
    public void setup() throws Exception {
        doReturn(schedulerService).when(platformServiceAccessor).getSchedulerService();
        doReturn(platformConfiguration).when(platformServiceAccessor).getPlatformConfiguration();
        doReturn(platformService).when(platformServiceAccessor).getPlatformService();
        doReturn(transactionService).when(platformServiceAccessor).getTransactionService();
        doReturn(platformServiceAccessor).when(platformAPI).getPlatformAccessor();
        doReturn(sessionAccessor).when(platformAPI).createSessionAccessor();
        doReturn(tenants).when(platformAPI).getTenants(platformServiceAccessor);
        doReturn(bonitaHomeServer).when(platformAPI).getBonitaHomeServerInstance();
        PlatformAPIImpl.isNodeStarted = false;
    }

    @Test
    public void rescheduleErroneousTriggers_should_call_rescheduleErroneousTriggers() throws Exception {
        platformAPI.rescheduleErroneousTriggers();

        verify(schedulerService).rescheduleErroneousTriggers();
    }

    @Test(expected = UpdateException.class)
    public void rescheduleErroneousTriggers_should_throw_exception_when_rescheduleErroneousTriggers_failed() throws Exception {
        doThrow(new SSchedulerException("failed")).when(schedulerService).rescheduleErroneousTriggers();

        platformAPI.rescheduleErroneousTriggers();
    }

    @Test(expected = UpdateException.class)
    public void rescheduleErroneousTriggers_should_throw_exception_when_cant_getPlatformAccessor() throws Exception {
        doThrow(new IOException()).when(platformAPI).getPlatformAccessor();

        platformAPI.rescheduleErroneousTriggers();
    }

    @Test
    public void startNode_should_call_startScheduler_when_node_is_not_started() throws Exception {
        // Given
        doNothing().when(platformAPI).checkPlatformVersion(platformServiceAccessor);
        doNothing().when(platformAPI).startPlatformServices(platformServiceAccessor);
        doReturn(false).when(platformAPI).isNodeStarted();
        doReturn(Collections.singletonMap(sTenant, Collections.emptyList())).when(platformAPI)
                .beforeServicesStartOfRestartHandlersOfTenant(platformServiceAccessor, sessionAccessor, tenants);
        doNothing().when(platformAPI).startServicesOfTenants(platformServiceAccessor, sessionAccessor, tenants);
        doNothing().when(platformAPI).restartHandlersOfPlatform(platformServiceAccessor);
        doNothing().when(platformAPI).afterServicesStartOfRestartHandlersOfTenant(eq(platformServiceAccessor), anyMap());

        // When
        platformAPI.startNode();

        // Then
        verify(platformAPI).startScheduler(platformServiceAccessor);
    }

    @Test
    public void startNode_should_not_call_startScheduler_when_node_is_started() throws Exception {
        // Given
        doNothing().when(platformAPI).checkPlatformVersion(platformServiceAccessor);
        doNothing().when(platformAPI).startPlatformServices(platformServiceAccessor);
        doReturn(true).when(platformAPI).isNodeStarted();
        doNothing().when(platformAPI).startServicesOfTenants(platformServiceAccessor, sessionAccessor, tenants);

        // When
        platformAPI.startNode();

        // Then
        verify(platformAPI, never()).startScheduler(platformServiceAccessor);
    }

    @Test
    public void should_updateTenantPortalConfigurationFile_call_bonitaHomeServer() throws Exception {
        //when
        platformAPI.updateClientTenantConfigurationFile(TENANT_ID, "myProps.properties", "updated content".getBytes());
        //then
        verify(bonitaHomeServer).updateTenantPortalConfigurationFile(TENANT_ID, "myProps.properties", "updated content".getBytes());
    }

    @Test
    public void should_getTenantPortalConfigurationFile_call_bonitaHomeServer() {
        //given
        final String configurationFile = "a file";
        doReturn("content".getBytes()).when(bonitaHomeServer).getTenantPortalConfiguration(TENANT_ID, configurationFile);

        //when
        final byte[] configuration = platformAPI.getClientTenantConfiguration(TENANT_ID, configurationFile);

        //then
        assertThat(configuration).as("should return file content").isEqualTo("content".getBytes());
        verify(bonitaHomeServer).getTenantPortalConfiguration(TENANT_ID, configurationFile);
    }

    @Test
    public void should_deactivate_and_delete_tenant_when_cleaning_platform() throws Exception {
        //given
        STenantImpl tenant1 = new STenantImpl("t1", "john", 123342, "ACTIVATED", true);
        tenant1.setId(1L);
        STenantImpl tenant2 = new STenantImpl("t2", "john", 12335645, "ACTIVATED", false);
        tenant2.setId(2L);
        doReturn(Arrays.asList(tenant1,
                tenant2)).when(platformService).getTenants(any(QueryOptions.class));
        doNothing().when(platformAPI).deleteTenant(anyLong());
        //when
        platformAPI.cleanPlatform();
        //then
        verify(platformService).deactiveTenant(1L);
        verify(platformService).deactiveTenant(2L);
        verify(platformAPI).deleteTenant(1L);
        verify(platformAPI).deleteTenant(2L);
    }

    private static class MockedTransactionService implements TransactionService {

        @Override
        public void begin() {
        }

        @Override
        public void complete() {
        }

        public TransactionState getState() {
            return null;
        }

        @Override
        public boolean isTransactionActive() {
            return false;
        }

        @Override
        public void setRollbackOnly() {
        }

        @Override
        public boolean isRollbackOnly() {
            return false;
        }

        @Override
        public long getNumberOfActiveTransactions() {
            return 0;
        }

        @Override
        public <T> T executeInTransaction(Callable<T> callable) throws Exception {
            return callable.call();
        }

        @Override
        public void registerBonitaSynchronization(BonitaTransactionSynchronization txSync) {

        }

        @Override
        public void registerBeforeCommitCallable(Callable<Void> callable) {

        }

    }
}
