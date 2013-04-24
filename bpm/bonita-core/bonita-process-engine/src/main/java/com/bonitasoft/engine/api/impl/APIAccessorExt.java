/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api.impl;

import org.bonitasoft.engine.api.CommandAPI;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.impl.APIAccessorImpl;
import org.bonitasoft.engine.api.impl.CommandAPIImpl;
import org.bonitasoft.engine.api.impl.IdentityAPIImpl;

import com.bonitasoft.engine.api.APIAccessor;
import com.bonitasoft.engine.api.LogAPI;
import com.bonitasoft.engine.api.MonitoringAPI;
import com.bonitasoft.engine.api.NodeAPI;
import com.bonitasoft.engine.api.ProcessAPI;
import com.bonitasoft.engine.api.ReportAPI;

/**
 * @author Matthieu Chaffotte
 */
public class APIAccessorExt extends APIAccessorImpl implements APIAccessor {

    private static final long serialVersionUID = -7317110051980496939L;

    @Override
    public IdentityAPI getIdentityAPI() {
        return new IdentityAPIImpl();
    }

    @Override
    public ProcessAPI getProcessAPI() {
        return new ProcessAPIExt();
    }

    @Override
    public MonitoringAPI getMonitoringAPI() {
        return new MonitoringAPIImpl();
    }

    @Override
    public LogAPI getLogAPI() {
        return new LogAPIExt();
    }

    @Override
    public CommandAPI getCommandAPI() {
        return new CommandAPIImpl();
    }

    @Override
    public NodeAPI getNodeAPI() {
        return new NodeAPIImpl();
    }

    @Override
    public ReportAPI getReportAPI() {
        return new ReportAPIExt();
    }

}
