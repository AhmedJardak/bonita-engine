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
package org.bonitasoft.engine.platform.command.model.impl;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bonitasoft.engine.platform.command.model.SPlatformCommand;

/**
 * @author Zhang Bole
 */
@Data
@NoArgsConstructor
public class SPlatformCommandImpl implements SPlatformCommand {

    private long id;
    private long tenantId;
    private String name;
    private String description;
    private String implementation;

    public SPlatformCommandImpl(final String name, final String description, final String implementation) {
        super();
        this.name = name;
        this.description = description;
        this.implementation = implementation;
    }

    public SPlatformCommandImpl(final SPlatformCommand platformCommand) {
        super();
        this.id = platformCommand.getId();
        this.name = platformCommand.getName();
        this.description = platformCommand.getDescription();
        this.implementation = platformCommand.getImplementation();
    }

}
