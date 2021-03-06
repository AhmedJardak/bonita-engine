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
package org.bonitasoft.engine.core.process.instance.model.event.handling;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bonitasoft.engine.core.process.definition.model.event.trigger.SEventTriggerType;

/**
 * @author Elias Ricken de Medeiros
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SWaitingErrorEvent extends SWaitingEvent {

    private String errorCode;
    /**
     * the id of activity where the boundary event is attached to.
     */
    private long relatedActivityInstanceId;

    public SWaitingErrorEvent(final SBPMEventType eventType, final long processdefinitionId, final String processName, final long flowNodeDefinitionId,
                                  final String flowNodeName, final String errorCode) {
        super(eventType, processdefinitionId, processName, flowNodeDefinitionId, flowNodeName);
        this.errorCode = errorCode;
    }

    @Override
    public SEventTriggerType getEventTriggerType() {
        return SEventTriggerType.ERROR;
    }

}
