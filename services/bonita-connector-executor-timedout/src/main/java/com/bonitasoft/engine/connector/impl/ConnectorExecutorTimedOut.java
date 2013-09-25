/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.connector.impl;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bonitasoft.engine.connector.impl.ConnectorExecutorImpl;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.bonitasoft.engine.session.SessionService;
import org.bonitasoft.engine.sessionaccessor.SessionAccessor;

/**
 * Execute connectors in parallel thread with a timeout
 * 
 * @author Baptiste Mesta
 */
public class ConnectorExecutorTimedOut extends ConnectorExecutorImpl {

    public ConnectorExecutorTimedOut(final int queueCapacity, final int corePoolSize, final TechnicalLoggerService loggerService, final int maximumPoolSize,
            final long keepAliveTimeSeconds,
            final SessionAccessor sessionAccessor, final SessionService sessionService, final int timeout) {
        super(queueCapacity, corePoolSize, loggerService, maximumPoolSize, keepAliveTimeSeconds, sessionAccessor, sessionService);
        this.timeout = timeout;
    }

    private long timeout;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    @Override
    protected Map<String, Object> getValue(final Future<Map<String, Object>> submit) throws InterruptedException, ExecutionException, TimeoutException {
        return submit.get(getTimeout(), TimeUnit.SECONDS);
    }
}
