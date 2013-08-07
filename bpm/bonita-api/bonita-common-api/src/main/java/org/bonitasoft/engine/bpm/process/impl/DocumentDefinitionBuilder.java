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

import org.bonitasoft.engine.bpm.document.impl.DocumentDefinitionImpl;
import org.bonitasoft.engine.bpm.flownode.impl.FlowElementContainerDefinitionImpl;

/**
 * @author Baptiste Mesta
 * @author Matthieu Chaffotte
 */
public class DocumentDefinitionBuilder extends FlowElementContainerBuilder {

    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    private final DocumentDefinitionImpl documentDefinitionImpl;

    public DocumentDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final FlowElementContainerDefinitionImpl container,
            final String name, final String fileName) {
        this(processDefinitionBuilder, container, fileName); // FIXME : filename must be replaced by name ??
        documentDefinitionImpl.setFileName(fileName);
    }

    public DocumentDefinitionBuilder(final ProcessDefinitionBuilder processDefinitionBuilder, final FlowElementContainerDefinitionImpl container,
            final String name) {
        super(container, processDefinitionBuilder);
        documentDefinitionImpl = new DocumentDefinitionImpl(name);
        documentDefinitionImpl.setMimeType(DEFAULT_MIME_TYPE);
        container.addDocumentDefinition(documentDefinitionImpl);
    }

    public DocumentDefinitionBuilder addDescription(final String description) {
        documentDefinitionImpl.setDescription(description);
        return this;
    }

    public DocumentDefinitionBuilder addUrl(final String url) {
        if (documentDefinitionImpl.getFile() == null) {
            documentDefinitionImpl.setUrl(url);
        } else {
            getProcessBuilder().addError("Unable to add an url on a document that already have a file " + documentDefinitionImpl);
        }
        return this;
    }

    public DocumentDefinitionBuilder addContentFileName(final String contentFilename) {
        if (documentDefinitionImpl.getFileName() == null) {
            documentDefinitionImpl.setFileName(contentFilename);
        } else {
            getProcessBuilder().addError("Unable to add file name on a document that already have a file name " + documentDefinitionImpl);
        }
        return this;
    }

    public DocumentDefinitionBuilder addFile(final String file) {
        if (documentDefinitionImpl.getUrl() == null) {
            documentDefinitionImpl.setFile(file);
        } else {
            getProcessBuilder().addError("Unable to add a file on a document that already have an url");
        }
        return this;
    }

    public DocumentDefinitionBuilder addMimeType(final String mimeType) {
        documentDefinitionImpl.setMimeType(mimeType);
        return this;
    }

}
