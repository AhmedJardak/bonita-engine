/*******************************************************************************
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.page;

import org.bonitasoft.engine.exception.NotFoundException;

/**
 * @author Laurent Leseigneur
 *
 * @see org.bonitasoft.engine.page.PageNotFoundException
 *
 * @deprecated from version 7.0 on, use {@link org.bonitasoft.engine.page.PageNotFoundException} instead.
 */
@Deprecated
public class PageNotFoundException extends NotFoundException {

    private static final long serialVersionUID = 2842457668242337487L;

    public PageNotFoundException(final String name) {
        super("Unable to find page with name: " + name);
    }

    public PageNotFoundException(final Throwable cause) {
        super(cause);
    }

}
