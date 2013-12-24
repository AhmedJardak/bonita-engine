/*******************************************************************************
 * Copyright (C) 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.theme.exception;

import org.bonitasoft.engine.exception.BonitaException;

/**
 * @author Celine Souchet
 */
public class RestoreThemeException extends BonitaException {

    private static final long serialVersionUID = 3465858452506324242L;

    public RestoreThemeException(final String message) {
        super(message);
    }

    public RestoreThemeException(final Throwable cause) {
        super(cause);
    }

    public RestoreThemeException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
