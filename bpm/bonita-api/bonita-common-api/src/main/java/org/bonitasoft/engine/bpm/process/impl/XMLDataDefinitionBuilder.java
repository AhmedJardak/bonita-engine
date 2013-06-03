/**
 * Copyright (C) 2012 BonitaSoft S.A.
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
package org.bonitasoft.engine.bpm.process.impl;

import org.bonitasoft.engine.bpm.data.impl.XMLDataDefinitionImpl;
import org.bonitasoft.engine.bpm.flownode.impl.ActivityDefinitionImpl;
import org.bonitasoft.engine.bpm.flownode.impl.CatchMessageEventTriggerDefinitionImpl;
import org.bonitasoft.engine.bpm.flownode.impl.FlowElementContainerDefinitionImpl;
import org.bonitasoft.engine.expression.Expression;

/**
 * @author Feng Hui
 * @author Matthieu Chaffotte
 */
public class XMLDataDefinitionBuilder extends DataDefinitionBuilder {

    public XMLDataDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final FlowElementContainerDefinitionImpl process,
            final String name, final String className, final Expression defaultValue) {
        super(processDefinitionBuilder, process, getTextData(processDefinitionBuilder, name, className, defaultValue));
        processDefinitionBuilder.checkExpression(toString(), defaultValue);
    }

    public XMLDataDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final FlowElementContainerDefinitionImpl process,
            final ActivityDefinitionImpl activity, final String name, final String className, final Expression defaultValue) {
        super(processDefinitionBuilder, process, activity, getTextData(processDefinitionBuilder, name, className, defaultValue));
        processDefinitionBuilder.checkExpression(toString(), defaultValue);
    }

    public XMLDataDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final FlowElementContainerDefinitionImpl container,
            final CatchMessageEventTriggerDefinitionImpl messageEventTrigger, final String name, final String className, final Expression defaultValue) {
        super(processDefinitionBuilder, container, messageEventTrigger, getTextData(processDefinitionBuilder, name, className, defaultValue));
        processDefinitionBuilder.checkExpression(toString(), defaultValue);
    }

    private static XMLDataDefinitionImpl getTextData(final ProcessDefinitionBuilder processDefinitionBuilder, final String name, final String className,
            final Expression defaultValue) {
        final XMLDataDefinitionImpl xml = new XMLDataDefinitionImpl(name, defaultValue);
        xml.setClassName(className);
        return xml;
    }

    public XMLDataDefinitionBuilder setNamespace(final String nameSpace) {
        ((XMLDataDefinitionImpl) getDataDefinition()).setNamespace(nameSpace);
        return this;
    }

    public XMLDataDefinitionBuilder setElement(final String element) {
        ((XMLDataDefinitionImpl) getDataDefinition()).setElement(element);
        return this;
    }

}
