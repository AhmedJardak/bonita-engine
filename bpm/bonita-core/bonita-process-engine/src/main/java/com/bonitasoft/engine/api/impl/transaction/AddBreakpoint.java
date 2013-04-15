/*******************************************************************************
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api.impl.transaction;

import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.transaction.TransactionContentWithResult;

import com.bonitasoft.engine.core.process.instance.api.BreakpointService;
import com.bonitasoft.engine.core.process.instance.model.breakpoint.SBreakpoint;
import com.bonitasoft.engine.core.process.instance.model.builder.BPMInstanceBuilders;

/**
 * @author Baptiste Mesta
 */
public class AddBreakpoint implements TransactionContentWithResult<SBreakpoint> {

    private final BreakpointService breakpointService;

    private SBreakpoint breakpoint;

    public AddBreakpoint(final BreakpointService breakpointService, final BPMInstanceBuilders breakpointBuilder, final long definitionId,
            final long instanceId, final String elementName, final int idOfTheStateToInterrupt, final int idOfTheInterruptingState) {
        this.breakpointService = breakpointService;
        breakpoint = breakpointBuilder.getSBreakpointBuilder()
                .createNewInstance(definitionId, instanceId, elementName, idOfTheStateToInterrupt, idOfTheInterruptingState).done();
    }

    public AddBreakpoint(final BreakpointService breakpointService, final BPMInstanceBuilders breakpointBuilder, final long definitionId,
            final String elementName, final int idOfTheStateToInterrupt, final int idOfTheInterruptingState) {
        this.breakpointService = breakpointService;
        breakpoint = breakpointBuilder.getSBreakpointBuilder().createNewInstance(definitionId, elementName, idOfTheStateToInterrupt, idOfTheInterruptingState)
                .done();
    }

    @Override
    public void execute() throws SBonitaException {
        breakpoint = breakpointService.addBreakpoint(breakpoint);
    }

    @Override
    public SBreakpoint getResult() {
        return breakpoint;
    }

}
