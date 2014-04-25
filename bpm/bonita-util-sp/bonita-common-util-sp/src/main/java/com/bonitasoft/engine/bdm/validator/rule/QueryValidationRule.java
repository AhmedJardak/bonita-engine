/*******************************************************************************
 * Copyright (C) 2014 Bonitasoft S.A.
 * Bonitasoft is a trademark of Bonitasoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * Bonitasoft, 32 rue Gustave Eiffel 38000 Grenoble
 * or Bonitasoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.bdm.validator.rule;

import javax.lang.model.SourceVersion;

import com.bonitasoft.engine.bdm.Query;
import com.bonitasoft.engine.bdm.validator.ValidationStatus;

public class QueryValidationRule implements ValidationRule {

    private static final int MAX_QUERY_NAME_LENGTH = 150;

    @Override
    public boolean appliesTo(final Object modelElement) {
        return modelElement instanceof Query;
    }

    @Override
    public ValidationStatus checkRule(final Object modelElement) {
        if (!appliesTo(modelElement)) {
            throw new IllegalArgumentException(QueryValidationRule.class.getName() + " doesn't handle validation for " + modelElement.getClass().getName());
        }
        final Query query = (Query) modelElement;
        final ValidationStatus status = new ValidationStatus();
        final String name = query.getName();
        if (name == null || name.isEmpty()) {
            status.addError("A query must have name");
            return status;
        }
        final int index = name.indexOf('.') + 1;
        final String queryName = name.substring(index);
        if (!SourceVersion.isIdentifier(queryName)) {
            status.addError(name + " is not a valid Java identifier.");
        }
        if (name.length() > MAX_QUERY_NAME_LENGTH) {
            status.addError(name + " length must be lower than 150 characters.");
        }
        if (query.getContent() == null || query.getContent().isEmpty()) {
            status.addError(name + " query must have a content defined");
        }
        if (query.getReturnType() == null || query.getReturnType().isEmpty()) {
            status.addError(name + " query must have a return type defined");
        }

        return status;
    }

}
