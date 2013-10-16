/**
 * Copyright (C) 2013 BonitaSoft S.A.
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
package org.bonitasoft.engine.document.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bonitasoft.engine.document.SDocumentContentNotFoundException;
import org.bonitasoft.engine.document.SDocumentException;
import org.bonitasoft.engine.document.model.SDocumentBuilders;
import org.bonitasoft.engine.document.model.SDocumentContent;
import org.bonitasoft.engine.events.model.builders.SEventBuilders;
import org.bonitasoft.engine.persistence.ReadPersistenceService;
import org.bonitasoft.engine.persistence.SBonitaReadException;
import org.bonitasoft.engine.persistence.SelectOneDescriptor;
import org.bonitasoft.engine.recorder.Recorder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Celine Souchet
 */
public class DocumentServiceImplTest {

    private Recorder recorder;

    private ReadPersistenceService persistence;

    private SEventBuilders eventBuilders;

    private SDocumentBuilders documentBuilders;

    private DocumentServiceImpl documentServiceImpl;

    @Before
    public void setUp() {
        recorder = mock(Recorder.class);
        persistence = mock(ReadPersistenceService.class);
        eventBuilders = mock(SEventBuilders.class);
        documentBuilders = mock(SDocumentBuilders.class);
        documentServiceImpl = new DocumentServiceImpl(recorder, eventBuilders, persistence, documentBuilders);
    }

    /**
     * Test method for {@link org.bonitasoft.engine.document.impl.DocumentServiceImpl#getContent(java.lang.String)}.
     */
    @Test
    public final void getContent() throws SBonitaReadException, SDocumentContentNotFoundException, SDocumentException {
        final SDocumentContent sDocumentContent = mock(SDocumentContent.class);
        final byte[] content = { 2 };
        when(sDocumentContent.getContent()).thenReturn(content);
        when(persistence.selectOne(any(SelectOneDescriptor.class))).thenReturn(sDocumentContent);

        Assert.assertEquals(content, documentServiceImpl.getContent("documentId"));
    }

    @Test(expected = SDocumentContentNotFoundException.class)
    public final void getContentNotExists() throws SBonitaReadException, SDocumentContentNotFoundException, SDocumentException {
        when(persistence.selectOne(any(SelectOneDescriptor.class))).thenReturn(null);

        documentServiceImpl.getContent("documentId");
    }

    @Test(expected = SDocumentException.class)
    public final void getContentThrowException() throws SBonitaReadException, SDocumentContentNotFoundException, SDocumentException {
        when(persistence.selectOne(any(SelectOneDescriptor.class))).thenThrow(new SBonitaReadException(""));

        documentServiceImpl.getContent("documentId");
    }

}
