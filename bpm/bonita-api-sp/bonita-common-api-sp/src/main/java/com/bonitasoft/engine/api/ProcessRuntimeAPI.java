/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.bpm.connector.ConnectorExecutionException;
import org.bonitasoft.engine.bpm.connector.ConnectorInstanceNotFoundException;
import org.bonitasoft.engine.bpm.connector.ConnectorInstanceWithFailureInfo;
import org.bonitasoft.engine.bpm.connector.ConnectorNotFoundException;
import org.bonitasoft.engine.bpm.flownode.ManualTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstanceNotFoundException;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.expression.Expression;
import org.bonitasoft.engine.operation.Operation;
import org.bonitasoft.engine.session.InvalidSessionException;

import com.bonitasoft.engine.bpm.flownode.ManualTaskCreator;
import com.bonitasoft.engine.bpm.process.Index;
import com.bonitasoft.engine.bpm.process.impl.ProcessInstanceUpdater;

/**
 * @author Matthieu Chaffotte
 */
public interface ProcessRuntimeAPI extends org.bonitasoft.engine.api.ProcessRuntimeAPI {

    /**
     * Add a manual task with given human task id.
     * 
     * @param creator
     *            the manual task creator
     * @return the matching an instance of manual task
     * @throws CreationException
     * @throws AlreadyExistsException
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     *             since 6.0
     */
    ManualTaskInstance addManualUserTask(ManualTaskCreator creator) throws CreationException, AlreadyExistsException;

    /**
     * Delete a manual task. Only manual tasks can be deleted at runtime.
     * 
     * @param manualTaskId
     *            the id of the task to delete
     * @throws DeletionException
     *             if the manual task could not be deleted.
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     */
    void deleteManualUserTask(final long manualTaskId) throws DeletionException;

    /**
     * Execute connector in given process instance initialized.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evaluate the connector
     * @param processInstanceId
     *            Identifier of the process instance
     * @return a map with connector parameter name and parameter value object
     * @throws ConnectorExecutionException
     *             if an error occurs when trying to execute the connector
     * @throws ConnectorNotFoundException
     *             if the specified connector is not found
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorAtProcessInstantiation(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, long processInstanceId)
            throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given process instance initialized with operations.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evaluate the connector
     * @param operations
     *            map of operations having each a special context (input values)
     * @param operationsInputValues
     *            all parameters values for operations
     * @param processInstanceId
     *            Identifier of the process instance
     * @return a map with new values of elements set by the operations
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorAtProcessInstantiation(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, List<Operation> operations,
            Map<String, Serializable> operationsInputValues, long processInstanceId) throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given activity instance.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evalute the connector
     * @param activityInstanceId
     *            Identifier of the activity instance
     * @return a map with connector parameter name and parameter value object
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorOnActivityInstance(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, long activityInstanceId)
            throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given activity instance.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evaluate the connector
     * @param operations
     *            map of operations having each a special context (input values)
     * @param operationsInputValues
     *            TODO
     * @param activityInstanceId
     *            Identifier of the activity instance
     * @return a map with new values of elements set by the operations
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorOnActivityInstance(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, List<Operation> operations,
            Map<String, Serializable> operationsInputValues, long activityInstanceId) throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given activity instance finished.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evalute the connector
     * @param activityInstanceId
     *            Identifier of the activity instance
     * @return a map with connector parameter name and parameter value object
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorOnCompletedActivityInstance(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, long activityInstanceId)
            throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given activity instance finished.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evalute the connector
     * @param operations
     *            map of operations having each a special context (input values)
     * @param operationsInputValues
     *            TODO
     * @param activityInstanceId
     *            Identifier of the activity instance
     * @return a map with new values of elements set by the operations
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorOnCompletedActivityInstance(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, List<Operation> operations,
            Map<String, Serializable> operationsInputValues, long activityInstanceId) throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given process instance finished.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evalute the connector
     * @param processInstanceId
     *            Identifier of the process instance
     * @return a map with connector parameter name and parameter value object
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorOnCompletedProcessInstance(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, long processInstanceId)
            throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given process instance finished with operations.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evalute the connector
     * @param operations
     *            map of operations having each a special context (input values)
     * @param operationsInputValues
     *            TODO
     * @param processInstanceId
     *            Identifier of the process instance
     * @return a map with new values of elements set by the operations
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorOnCompletedProcessInstance(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, List<Operation> operations,
            Map<String, Serializable> operationsInputValues, long processInstanceId) throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given process instance.
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evalute the connector
     * @param processInstanceId
     *            Identifier of the process instance
     * @return a map with connector parameter name and parameter value object
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorOnProcessInstance(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, long processInstanceId)
            throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Execute connector in given process instance with operations
     * 
     * @param connectorDefinitionId
     *            Identifier of connector definition
     * @param connectorDefinitionVersion
     *            version of the connector definition
     * @param connectorInputParameters
     *            all expressions related with the connector
     * @param inputValues
     *            all parameters values for expression need when evalute the connector
     * @param processInstanceId
     *            Identifier of the process instance
     * @param operations
     *            map of operations having each a special context (input values)
     * @param operationsInputValues
     * @return a map with new values of elements set by the operations
     * @throws ConnectorExecutionException
     *             TODO
     * @throws ConnectorNotFoundException
     *             TODO
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    Map<String, Serializable> executeConnectorOnProcessInstance(String connectorDefinitionId, String connectorDefinitionVersion,
            Map<String, Expression> connectorInputParameters, Map<String, Map<String, Serializable>> inputValues, List<Operation> operations,
            Map<String, Serializable> operationsInputValues, long processInstanceId) throws ConnectorExecutionException, ConnectorNotFoundException;

    /**
     * Update an index of a process instance.
     * 
     * @param processInstanceId
     *            identifier of the process instance
     * @param index
     *            which index to update
     * @param value
     *            the new value for the index
     * @return the updated process instance
     * @throws ProcessInstanceNotFoundException
     *             Error thrown if no process instance have an id corresponding to the value of processInstanceId parameter.
     * @throws UpdateException
     *             if an error is thrown while updating the process instance.
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    ProcessInstance updateProcessInstanceIndex(long processInstanceId, Index index, String value) throws ProcessInstanceNotFoundException, UpdateException;

    /**
     * Update an instance of process with the given processInstanceId.
     * 
     * @param processInstanceId
     *            Identifier of the process instance
     * @param updater
     *            including new value of all attributes adaptable
     * @return the process instance updated
     * @throws ProcessInstanceNotFoundException
     *             Error thrown if no process instance have an id corresponding to the value of processInstanceId parameter.
     * @throws UpdateException
     *             if an error is thrown while updating the process instance.
     * @throws InvalidSessionException
     *             if the session is invalid, e.g. the session has expired.
     * @since 6.0
     */
    ProcessInstance updateProcessInstance(long processInstanceId, ProcessInstanceUpdater updater) throws ProcessInstanceNotFoundException, UpdateException;
    
    /**
     * Retrieves a <code>ConnectorInstanceWithFailureInfo</code> specified by its identifier.
     * 
     * @param connectorInstanceId
     *            the identifier of the <code>ConnectorInstanceWithFailureInfo</code> to be retrieved.
     * @return the <code>ConnectorInstanceWithFailureInfo</code> instance.
     * @throws ConnectorInstanceNotFoundException
     *             if no <code>ConnectorInstanceWithFailureInfo</code> is found with the specified connectorInstanceId.
     * @since 6.1
     */
    ConnectorInstanceWithFailureInfo getConnectorInstanceWithFailureInformation(long connectorInstanceId) throws ConnectorInstanceNotFoundException;

}
