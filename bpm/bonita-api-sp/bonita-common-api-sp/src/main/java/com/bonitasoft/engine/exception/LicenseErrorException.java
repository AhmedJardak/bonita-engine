/**
 * Copyright (C) 2015 Bonitasoft S.A.
 * Bonitasoft is a trademark of Bonitasoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * Bonitasoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or Bonitasoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 **/

package com.bonitasoft.engine.exception;

import org.bonitasoft.engine.exception.BonitaRuntimeException;

/**
 * Indicates that a License problem has occured
 * @author Elias Ricken de Medeiros
 */
public class LicenseErrorException extends BonitaRuntimeException {

    public LicenseErrorException(final String message) {
        super(message, null, true, false);
    }

}
