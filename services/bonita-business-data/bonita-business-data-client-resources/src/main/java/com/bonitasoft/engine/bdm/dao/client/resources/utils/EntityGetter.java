package com.bonitasoft.engine.bdm.dao.client.resources.utils;

import static com.bonitasoft.engine.bdm.dao.client.resources.utils.Capitalizer.capitalize;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.bonitasoft.engine.bdm.model.field.Field;

/**
 * Wrapper over entity getter method
 * 
 * @author Colin Puy
 */
public class EntityGetter {

    private Method method;

    public EntityGetter(Method method) {
        checkIsGetter(method);
        this.method = method;
    }

    private void checkIsGetter(Method method) {
        String methodName = method.getName();
        if (!methodName.startsWith("get") || methodName.length() <= 3) {
            throw new IllegalArgumentException(methodName + " is not a valid getter name.");
        }
    }

    public String getSourceEntityName() {
        return method.getDeclaringClass().getSimpleName();
    }

    public String getCapitalizedFieldName() {
        return method.getName().substring(3);
    }
    
    public String getReturnTypeClassName() {
        if (returnsList()) {
            return List.class.getName();
        } else {
            return method.getReturnType().getName();
        }
    }

    public String getAssociatedNamedQuery() {
        String targetEntityName = getTargetEntityClass().getSimpleName();
        return targetEntityName + ".find" + getCapitalizedFieldName() + "By" + getSourceEntityName() + capitalize(Field.PERSISTENCE_ID);
    }

    public boolean returnsList() {
        Class<?> returnTypeClass = method.getReturnType();
        return List.class.isAssignableFrom(returnTypeClass);
    }

    public Class<?> getTargetEntityClass() {
        if (returnsList()) {
            final ParameterizedType listType = (ParameterizedType) method.getGenericReturnType();
            final Class<?> type = (Class<?>) listType.getActualTypeArguments()[0];
            return type;
        } else {
            return method.getReturnType();
        }
    }
}
