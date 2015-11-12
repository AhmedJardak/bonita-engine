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
package org.bonitasoft.engine.commons.exceptions;

/**
 * @author Baptiste Mesta
 */
public class SObjectReadException extends SBonitaException {

    private static final long serialVersionUID = -183875236263807679L;

    /**
     * 
     */
    public SObjectReadException() {
        super();
    }

    /**
     * @param arguments
     */
    public SObjectReadException(final Object... arguments) {
        super(arguments);
    }

    /**
     * @param message
     * @param cause
     */
    public SObjectReadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public SObjectReadException(final String message) {
        super(message);
    }

    /**
     * @param cause
     * @param arguments
     */
    public SObjectReadException(final Throwable cause, final Object... arguments) {
        super(cause, arguments);
    }

    /**
     * @param cause
     */
    public SObjectReadException(final Throwable cause) {
        super(cause);
    }

}