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
package org.bonitasoft.engine.test.persistence.builder;

import org.bonitasoft.engine.identity.model.SCustomUserInfoDefinition;
import org.bonitasoft.engine.identity.model.SCustomUserInfoDefinition;


/**
 * @author Elias Ricken de Medeiros
 *
 */
public class CustomUserInfoDefinitionBuilder extends PersistentObjectBuilder<SCustomUserInfoDefinition, CustomUserInfoDefinitionBuilder> {

    private String name;
    
    public static CustomUserInfoDefinitionBuilder aCustomUserInfoDefinition() {
        return new CustomUserInfoDefinitionBuilder();
    }

    @Override
    CustomUserInfoDefinitionBuilder getThisBuilder() {
        return this;
    }

    @Override
    SCustomUserInfoDefinition _build() {
        SCustomUserInfoDefinition infoDef = new SCustomUserInfoDefinition();
        infoDef.setName(name);
        return infoDef;
    }
    
    public CustomUserInfoDefinitionBuilder withName(String name) {
        this.name = name;
        return this;
    }

}
