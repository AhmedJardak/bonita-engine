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

package org.bonitasoft.engine.connector.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.bonitasoft.engine.connector.AbstractSConnector;
import org.bonitasoft.engine.connector.SConnector;
import org.bonitasoft.engine.connector.exception.SConnectorException;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerSLF4JImpl;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.bonitasoft.engine.session.SessionService;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;
import org.bonitasoft.engine.tracking.TimeTracker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorExecutorImplTest {

    @Mock
    private TechnicalLoggerService loggerService;

    @Mock
    private SessionAccessor sessionAccessor;

    @Mock
    private SessionService sessionService;

    @Mock
    private SConnector connector;

    @Mock
    private TimeTracker timeTracker;

    private ConnectorExecutorImpl connectorExecutorImpl;

    @Before
    public void before() {
        connectorExecutorImpl = new ConnectorExecutorImpl(1, 1, loggerService, 1, 1, sessionAccessor, sessionService, timeTracker);
        connectorExecutorImpl.start();
        doReturn(true).when(loggerService).isLoggable(any(Class.class), any(TechnicalLogSeverity.class));
    }

    @Test
    public void should_execute_submit_callable() throws Exception {
        SConnector connector = new SConnector() {
            @Override
            public void setInputParameters(Map<String, Object> parameters) {

            }

            @Override
            public void validate() {

            }

            @Override
            public Map<String, Object> execute() {
                return Collections.singletonMap("result", "resultValue");
            }

            @Override
            public void connect() {

            }

            @Override
            public void disconnect() {

            }
        };
        when(loggerService.asLogger(any())).thenReturn(new TechnicalLoggerSLF4JImpl().asLogger(this.getClass()));
        final Map<String, Object> result = connectorExecutorImpl.execute(connector,
                Collections.singletonMap("key", "value"), Thread.currentThread().getContextClassLoader()).get(100,TimeUnit.MILLISECONDS);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get("result")).isEqualTo("resultValue");
    }

    @Test(expected = SConnectorException.class)
    public void should_execute_throw_exception_when_not_started() throws Exception {
        // given
        connectorExecutorImpl.stop();
        // when
        connectorExecutorImpl.execute(connector, Collections.singletonMap("key", "value"),
                Thread.currentThread().getContextClassLoader());
    }

    @Test
    public void should_disconnect_call_disconnect_on_connector() throws Exception {
        // when
        connectorExecutorImpl.disconnect(connector);
        // then
        verify(connector).disconnect();
    }

    @Test
    public void should_disconnect_rethrow_connector_exceptions() throws Exception {
        // given
        final SConnectorException exception = new SConnectorException("myException");
        doThrow(exception).when(connector).disconnect();
        // when
        try {
            connectorExecutorImpl.disconnect(connector);
            fail("should have thrown the exception");
        } catch (final SConnectorException e) {
            // then
            assertThat(e).isEqualTo(exception);
        }
    }

    @Test
    public void should_disconnectSilently_only_logException() throws Exception {
        // given
        final SConnectorException exception = new SConnectorException("myException");
        doThrow(exception).when(connector).disconnect();
        // when
        connectorExecutorImpl.disconnectSilently(connector);
        // then
        verify(loggerService).log(ConnectorExecutorImpl.class, TechnicalLogSeverity.WARNING,
                "An error occurred while disconnecting the connector: " + connector, exception);
    }

    @Test
    public void should_stop_await_termination_of_thread_pool() {
        ExecutorService executorService = connectorExecutorImpl.getExecutorService();

        connectorExecutorImpl.stop();

        assertThat(executorService.isShutdown()).isTrue();
    }

    @Test
    public void pause_should_await_termination_of_thread_pool() {
        ExecutorService executorService = connectorExecutorImpl.getExecutorService();

        connectorExecutorImpl.stop();

        assertThat(executorService.isShutdown()).isTrue();
    }

    @Test
    public void start_should_await_termination_of_thread_pool() {
        // when
        // start in before

        // then
        assertThat(connectorExecutorImpl.getExecutorService()).as("The executor service must be not null.").isNotNull();
    }

    @Test
    public void resume_should_await_termination_of_thread_pool() {
        // when
        connectorExecutorImpl.pause();
        connectorExecutorImpl.resume();

        // then
        assertThat(connectorExecutorImpl.getExecutorService()).as("The executor service must be not null.").isNotNull();
    }

    @Test
    public void should_update_connectors_counters_when_adding_a_connector_with_immediate_execution() throws Exception {
        //when:
        when(loggerService.asLogger(any())).thenReturn(new TechnicalLoggerSLF4JImpl().asLogger(this.getClass()));
        connectorExecutorImpl.execute(new LocalSConnector(-1), new HashMap<>(), Thread.currentThread().getContextClassLoader()).get();
        TimeUnit.MILLISECONDS.sleep(50); // give some time to consider the connector to process

        //then:
        assertThat(connectorExecutorImpl.getExecuted()).as("Executed connectors number").isEqualTo(1);
        assertThat(connectorExecutorImpl.getRunnings()).as("Running connectors number").isEqualTo(0);
        assertThat(connectorExecutorImpl.getPendings()).as("Pending connectors number").isEqualTo(0);
    }

    @Test
    public void should_update_connectors_counters_when_enqueuing_connectors_with_long_processing_time()
            throws Exception {
        when(loggerService.asLogger(any())).thenReturn(new TechnicalLoggerSLF4JImpl().asLogger(this.getClass()));
        connectorExecutorImpl.execute(new LocalSConnector(2), new HashMap<>(), Thread.currentThread().getContextClassLoader());
        connectorExecutorImpl.execute(new LocalSConnector(2), new HashMap<>(), Thread.currentThread().getContextClassLoader());
        TimeUnit.MILLISECONDS.sleep(50); // give some time to consider the connector to process

        //then:  one is in queue (only one thread to execute connectors) and one is pending
        assertThat(connectorExecutorImpl.getExecuted()).as("Executed connectors number").isEqualTo(0);
        assertThat(connectorExecutorImpl.getRunnings()).as("Running connectors number").isEqualTo(1);
        assertThat(connectorExecutorImpl.getPendings()).as("Pending connectors number").isEqualTo(1);
    }

    // =================================================================================================================
    // UTILS
    // =================================================================================================================

    private static class LocalSConnector extends AbstractSConnector {

        private final long sleepPeriodInSeconds;

        private LocalSConnector(long sleepPeriodInSeconds) {
            this.sleepPeriodInSeconds = sleepPeriodInSeconds;
        }

        @Override
        public void validate() {
            // do nothing
        }

        @Override
        public Map<String, Object> execute() {
            if (sleepPeriodInSeconds > 0) {
                try {
                    TimeUnit.SECONDS.sleep(sleepPeriodInSeconds);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return new HashMap<>();
        }

        @Override
        public void connect() {
            // do nothing
        }

        @Override
        public void disconnect() {
            // do nothing
        }
    }

}
