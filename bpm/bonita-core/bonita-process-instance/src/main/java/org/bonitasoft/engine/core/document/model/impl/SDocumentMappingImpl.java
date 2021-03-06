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
package org.bonitasoft.engine.core.document.model.impl;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bonitasoft.engine.core.document.model.SDocumentMapping;

/**
 * @author Baptiste Mesta
 */
@Data
@NoArgsConstructor
public class SDocumentMappingImpl implements SDocumentMapping {

    private long id;
    private long tenantId;
    private long processInstanceId;
    private long documentId;
    private String name;
    private String description;
    private String version;
    private int index;

    public SDocumentMappingImpl(long documentId, long processInstanceId, String name) {
        this.documentId = documentId;
        this.processInstanceId = processInstanceId;
        this.name = name;
    }

}
