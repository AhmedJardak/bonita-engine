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
package org.bonitasoft.engine.event;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bonitasoft.engine.bpm.data.DataInstance;
import org.bonitasoft.engine.bpm.data.DataNotFoundException;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.FlowNodeInstance;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.SubProcessDefinition;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.bpm.process.impl.SubProcessDefinitionBuilder;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.expression.Expression;
import org.bonitasoft.engine.expression.ExpressionBuilder;
import org.bonitasoft.engine.expression.ExpressionEvaluationException;
import org.bonitasoft.engine.expression.InvalidExpressionException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.test.TestStates;
import org.bonitasoft.engine.test.annotation.Cover;
import org.bonitasoft.engine.test.annotation.Cover.BPMNConcept;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Baptiste Mesta
 * @author Elias Ricken de Medeiros
 */
public class ErrorEventSubProcessTest extends EventsAPITest {

    private User john;

    @Before
    public void beforeTest() throws BonitaException {
        login();
        john = createUser("john", "bpm");
        logout();
        loginWith("john", "bpm");

    }

    @After
    public void afterTest() throws BonitaException {
        deleteUser(john);
        logout();
    }

    private ProcessDefinition deployAndEnableProcessWithErrorEventSubProcess(final String catchErrorCode, final String throwErrorCode,
            final String subProcStartEventName) throws BonitaException {
        final Expression transitionCondition = new ExpressionBuilder().createDataExpression("throwException", Boolean.class.getName());
        final ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder().createNewInstance("ProcessWithEventSubProcess", "1.0");
        builder.addActor("mainActor");
        builder.addBooleanData("throwException", new ExpressionBuilder().createConstantBooleanExpression(true));
        builder.addStartEvent("start");
        builder.addUserTask("step1", "mainActor");
        builder.addUserTask("step2", "mainActor");
        builder.addEndEvent("end");
        builder.addEndEvent("endError").addErrorEventTrigger(throwErrorCode);
        builder.addTransition("start", "step1");
        builder.addTransition("start", "step2");
        builder.addTransition("step1", "end");
        builder.addTransition("step2", "endError", transitionCondition);
        builder.addDefaultTransition("step2", "end");
        final SubProcessDefinitionBuilder subProcessBuilder = builder.addSubProcess("eventSubProcess", true).getSubProcessBuilder();
        if (catchErrorCode == null) {
            subProcessBuilder.addStartEvent(subProcStartEventName).addErrorEventTrigger();
        } else {
            subProcessBuilder.addStartEvent(subProcStartEventName).addErrorEventTrigger(catchErrorCode);
        }
        subProcessBuilder.addUserTask("subStep", "mainActor");
        subProcessBuilder.addEndEvent("endSubProcess");
        subProcessBuilder.addTransition(subProcStartEventName, "subStep");
        subProcessBuilder.addTransition("subStep", "endSubProcess");
        final DesignProcessDefinition processDefinition = builder.done();
        return deployAndEnableWithActor(processDefinition, "mainActor", john);
    }

    private ProcessDefinition deployAndEnableProcessWithCallActivity(final String processName, final String targetProcessName, final String targetVersion)
            throws BonitaException {
        final Expression targetProcessExpr = new ExpressionBuilder().createConstantStringExpression(targetProcessName);
        final Expression targetVersionExpr = new ExpressionBuilder().createConstantStringExpression(targetVersion);
        final ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder().createNewInstance(processName, "1.0");
        builder.addActor("mainActor");
        builder.addStartEvent("start");
        builder.addCallActivity("callActivity", targetProcessExpr, targetVersionExpr);
        builder.addUserTask("step2", "mainActor");
        builder.addEndEvent("end");
        builder.addTransition("start", "callActivity");
        builder.addTransition("callActivity", "step2");
        builder.addTransition("step2", "end");
        final DesignProcessDefinition processDefinition = builder.done();
        return deployAndEnableWithActor(processDefinition, "mainActor", john);
    }

    private ProcessDefinition deployAndEnableProcessWithErrorEventSubProcessAndData(final String catchErrorCode, final String throwErroCode,
            final String subProcStartEventName) throws BonitaException {
        final ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder().createNewInstance("ProcessWithEventSubProcess", "1.0");
        builder.addShortTextData("content", new ExpressionBuilder().createConstantStringExpression("parentVar"));
        builder.addIntegerData("count", new ExpressionBuilder().createConstantIntegerExpression(1));
        builder.addActor("mainActor");
        builder.addStartEvent("start");
        builder.addUserTask("step1", "mainActor");
        builder.addUserTask("step2", "mainActor");
        builder.addEndEvent("end");
        builder.addEndEvent("endError").addErrorEventTrigger(throwErroCode);
        builder.addTransition("start", "step1");
        builder.addTransition("start", "step2");
        builder.addTransition("step1", "end");
        builder.addTransition("step2", "endError");
        final SubProcessDefinitionBuilder subProcessBuilder = builder.addSubProcess("eventSubProcess", true).getSubProcessBuilder();
        subProcessBuilder.addShortTextData("content", new ExpressionBuilder().createConstantStringExpression("childVar"));
        subProcessBuilder.addDoubleData("value", new ExpressionBuilder().createConstantDoubleExpression(10.0));
        subProcessBuilder.addStartEvent(subProcStartEventName).addErrorEventTrigger(catchErrorCode);
        subProcessBuilder.addUserTask("subStep", "mainActor").addShortTextData("content",
                new ExpressionBuilder().createConstantStringExpression("childActivityVar"));
        subProcessBuilder.addEndEvent("endSubProcess");
        subProcessBuilder.addTransition(subProcStartEventName, "subStep");
        subProcessBuilder.addTransition("subStep", "endSubProcess");
        final DesignProcessDefinition processDefinition = builder.done();
        return deployAndEnableWithActor(processDefinition, "mainActor", john);
    }

    private ProcessDefinition deployAndEnableProcWithErrorEvSubProcAndDataOnlyInRoot(final String errorCode, final String subProcStartEventName,
            final String rootUserTaskName, final String subProcUserTaskName, final String dataName, final String dataValue) throws BonitaException {
        final ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder().createNewInstance("ProcessWithEventSubProcess", "1.0");
        builder.addShortTextData(dataName, new ExpressionBuilder().createConstantStringExpression(dataValue));
        builder.addActor("mainActor");
        builder.addStartEvent("start");
        builder.addUserTask(rootUserTaskName, "mainActor");
        builder.addEndEvent("end");
        builder.addEndEvent("endError").addErrorEventTrigger(errorCode);
        builder.addTransition("start", rootUserTaskName);
        builder.addTransition(rootUserTaskName, "endError");
        final SubProcessDefinitionBuilder subProcessBuilder = builder.addSubProcess("eventSubProcess", true).getSubProcessBuilder();
        subProcessBuilder.addStartEvent(subProcStartEventName).addErrorEventTrigger(errorCode);
        subProcessBuilder.addUserTask(subProcUserTaskName, "mainActor");
        subProcessBuilder.addEndEvent("endSubProcess");
        subProcessBuilder.addTransition(subProcStartEventName, subProcUserTaskName);
        subProcessBuilder.addTransition(subProcUserTaskName, "endSubProcess");
        final DesignProcessDefinition processDefinition = builder.done();
        return deployAndEnableWithActor(processDefinition, "mainActor", john);
    }

    private ProcessDefinition deployAndEnableProcWithErrorEvSubProcAndDataOnlyInSubProc(final String errorCode, final String subProcStartEventName,
            final String rootUserTaskName, final String subProcUserTaskName, final String dataName, final String dataValue) throws BonitaException {
        final ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder().createNewInstance("ProcessWithEventSubProcess", "1.0");
        builder.addActor("mainActor");
        builder.addStartEvent("start");
        builder.addUserTask(rootUserTaskName, "mainActor");
        builder.addEndEvent("end");
        builder.addEndEvent("endError").addErrorEventTrigger(errorCode);
        builder.addTransition("start", rootUserTaskName);
        builder.addTransition(rootUserTaskName, "endError");
        final SubProcessDefinitionBuilder subProcessBuilder = builder.addSubProcess("eventSubProcess", true).getSubProcessBuilder();
        subProcessBuilder.addShortTextData(dataName, new ExpressionBuilder().createConstantStringExpression(dataValue));
        subProcessBuilder.addStartEvent(subProcStartEventName).addErrorEventTrigger(errorCode);
        subProcessBuilder.addUserTask(subProcUserTaskName, "mainActor");
        subProcessBuilder.addEndEvent("endSubProcess");
        subProcessBuilder.addTransition(subProcStartEventName, subProcUserTaskName);
        subProcessBuilder.addTransition(subProcUserTaskName, "endSubProcess");
        final DesignProcessDefinition processDefinition = builder.done();
        return deployAndEnableWithActor(processDefinition, "mainActor", john);
    }

    @Cover(classes = { SubProcessDefinition.class }, concept = BPMNConcept.EVENT_SUBPROCESS, keywords = { "event sub-process", "error" }, jira = "ENGINE-536")
    @Test
    public void testErrorEventSubProcessTriggeredNamedError() throws Exception {
        executeProcessTriggeringEventSubProcess("e1", "e1");
    }

    @Cover(classes = { SubProcessDefinition.class }, concept = BPMNConcept.EVENT_SUBPROCESS, keywords = { "event sub-process", "error" }, jira = "ENGINE-536")
    @Test
    public void testErrorEventSubProcessTriggeredCachAllErrors() throws Exception {
        executeProcessTriggeringEventSubProcess(null, "e1");
    }

    private void executeProcessTriggeringEventSubProcess(final String catchErrorCode, final String throwErrorCode) throws Exception {
        final String subProcStartEventName = "errorStart";
        final ProcessDefinition process = deployAndEnableProcessWithErrorEventSubProcess(catchErrorCode, throwErrorCode, subProcStartEventName);
        final ProcessInstance processInstance = getProcessAPI().startProcess(process.getId());
        final ActivityInstance step1 = waitForUserTask("step1", processInstance.getId());
        final ActivityInstance step2 = waitForUserTask("step2", processInstance.getId());
        List<ActivityInstance> activities = getProcessAPI().getActivities(processInstance.getId(), 0, 10);
        assertEquals(2, activities.size());
        checkNumberOfWaitingEvents(subProcStartEventName, 1);

        // throw error
        assignAndExecuteStep(step2, john.getId());
        waitForArchivedActivity(step2.getId(), TestStates.getNormalFinalState());

        final FlowNodeInstance eventSubProcessActivity = waitForFlowNodeInExecutingState(processInstance, "eventSubProcess", false);
        final ActivityInstance subStep = waitForUserTask("subStep", processInstance.getId());
        final ProcessInstance subProcInst = getProcessAPI().getProcessInstance(subStep.getParentProcessInstanceId());

        activities = getProcessAPI().getActivities(processInstance.getId(), 0, 10);
        assertEquals(2, activities.size());
        // the parent process instance is supposed to be aborted, so no more waiting events are expected
        checkNumberOfWaitingEvents(subProcStartEventName, 0);

        waitForArchivedActivity(step1.getId(), TestStates.getAbortedState());
        assignAndExecuteStep(subStep, john.getId());
        waitForArchivedActivity(eventSubProcessActivity.getId(), TestStates.getNormalFinalState());
        waitForProcessToFinish(subProcInst);
        waitForProcessToFinish(processInstance, TestStates.getAbortedState());

        // check that the transition wasn't taken
        checkWasntExecuted(processInstance, "end");

        disableAndDeleteProcess(process.getId());
    }

    @Cover(classes = { SubProcessDefinition.class }, concept = BPMNConcept.EVENT_SUBPROCESS, keywords = { "event sub-process", "error" }, jira = "ENGINE-536")
    @Test
    public void testErrorEventSubProcessNotTriggered() throws Exception {
        final String errorCode = "error 1";
        final String subProcStartEventName = "errorStart";
        final ProcessDefinition process = deployAndEnableProcessWithErrorEventSubProcess(errorCode, errorCode, subProcStartEventName);
        final ProcessInstance processInstance = getProcessAPI().startProcess(process.getId());
        final ActivityInstance step1 = waitForUserTask("step1", processInstance.getId());
        final ActivityInstance step2 = waitForUserTask("step2", processInstance.getId());
        getProcessAPI().getProcessDataInstance("throwException", processInstance.getId());
        getProcessAPI().updateProcessDataInstance("throwException", processInstance.getId(), false);
        final List<ActivityInstance> activities = getProcessAPI().getActivities(processInstance.getId(), 0, 10);
        assertEquals(2, activities.size());
        checkNumberOfWaitingEvents(subProcStartEventName, 1);

        assignAndExecuteStep(step1, john.getId());
        assignAndExecuteStep(step2, john.getId());

        waitForArchivedActivity(step1.getId(), TestStates.getNormalFinalState());
        waitForProcessToFinish(processInstance);

        // the parent process instance has completed, so no more waiting events are expected
        checkNumberOfWaitingEvents(subProcStartEventName, 0);

        disableAndDeleteProcess(process.getId());
    }

    @Cover(classes = { SubProcessDefinition.class }, concept = BPMNConcept.EVENT_SUBPROCESS, keywords = { "event sub-process", "error" }, jira = "ENGINE-536")
    @Test
    public void testCreateSeveralInstances() throws Exception {
        final String errorCode = "e2";
        final String subProcStartEventName = "errorStart";
        final ProcessDefinition process = deployAndEnableProcessWithErrorEventSubProcess(errorCode, errorCode, subProcStartEventName);
        final ProcessInstance processInstance1 = getProcessAPI().startProcess(process.getId());
        final ProcessInstance processInstance2 = getProcessAPI().startProcess(process.getId());

        // throw error
        waitForUserTask("step1", processInstance1.getId());
        waitForUserTaskAndExecuteIt("step2", processInstance1.getId(), john.getId());

        waitForUserTask("step1", processInstance2.getId());
        waitForUserTaskAndExecuteIt("step2", processInstance2.getId(), john.getId());

        waitForUserTask("subStep", processInstance1.getId());
        waitForUserTask("subStep", processInstance2.getId());

        disableAndDeleteProcess(process.getId());
    }

    @Cover(classes = { SubProcessDefinition.class }, concept = BPMNConcept.EVENT_SUBPROCESS, keywords = { "event sub-process", "error", "parent process data" }, jira = "ENGINE-536")
    @Test
    public void testSubProcessCanAccessParentData() throws Exception {
        final String errorCode = "error1";
        final String subProcStartEventName = "errorStart";
        final ProcessDefinition process = deployAndEnableProcessWithErrorEventSubProcessAndData(errorCode, errorCode, subProcStartEventName);
        final ProcessInstance processInstance = getProcessAPI().startProcess(process.getId());
        waitForUserTask("step1", processInstance.getId());
        waitForUserTaskAndExecuteIt("step2", processInstance.getId(), john.getId());

        final ActivityInstance subStep = waitForUserTask("subStep", processInstance.getId());
        final ProcessInstance subProcInst = getProcessAPI().getProcessInstance(subStep.getParentProcessInstanceId());
        checkRetrieveDataInstances(processInstance, subStep, subProcInst);

        checkEvaluateExpression(subStep, "count", Integer.class, 1);

        assignAndExecuteStep(subStep, john.getId());
        waitForProcessToFinish(subProcInst);
        waitForProcessToFinish(processInstance, TestStates.getAbortedState());

        disableAndDeleteProcess(process.getId());
    }

    @Cover(classes = { SubProcessDefinition.class }, concept = BPMNConcept.EVENT_SUBPROCESS, keywords = { "event sub-process", "error", "parent process data" }, jira = "ENGINE-1397")
    @Test
    public void testSubProcessCanAccessParentDataEvenIfItDoesntHaveLocalData() throws Exception {
        final String errorCode = "error1";
        final String subProcStartEventName = "errorStart";
        final String rootUserTaskName = "step1";
        final String subProcUserTaskName = "subStep";
        final String dataName = "content";
        final String dataValue = "default";
        final ProcessDefinition process = deployAndEnableProcWithErrorEvSubProcAndDataOnlyInRoot(errorCode, subProcStartEventName, rootUserTaskName,
                subProcUserTaskName, dataName, dataValue);
        final ProcessInstance processInstance = getProcessAPI().startProcess(process.getId());
        waitForUserTaskAndExecuteIt(rootUserTaskName, processInstance, john);

        final ActivityInstance subStep = waitForUserTask(subProcUserTaskName, processInstance);
        final ProcessInstance subProcInst = getProcessAPI().getProcessInstance(subStep.getParentProcessInstanceId());
        checkProcessDataInstance(dataName, subProcInst.getId(), dataValue);
        checkProcessDataInstance(dataName, processInstance.getId(), dataValue);

        checkEvaluateExpression(subStep, dataName, String.class, dataValue);

        assignAndExecuteStep(subStep, john.getId());
        waitForProcessToFinish(subProcInst);
        waitForProcessToFinish(processInstance, TestStates.getAbortedState());

        disableAndDeleteProcess(process.getId());
    }

    @Cover(classes = { SubProcessDefinition.class }, concept = BPMNConcept.EVENT_SUBPROCESS, keywords = { "event sub-process", "error", "parent process data" }, jira = "ENGINE-1397")
    @Test
    public void testEventSubProcesWithDataAndRootProcessWithNoData() throws Exception {
        final String errorCode = "error1";
        final String subProcStartEventName = "errorStart";
        final String rootUserTaskName = "step1";
        final String subProcUserTaskName = "subStep";
        final String dataName = "content";
        final String dataValue = "default";
        final ProcessDefinition process = deployAndEnableProcWithErrorEvSubProcAndDataOnlyInSubProc(errorCode, subProcStartEventName, rootUserTaskName,
                subProcUserTaskName, dataName, dataValue);
        final ProcessInstance processInstance = getProcessAPI().startProcess(process.getId());
        waitForUserTaskAndExecuteIt(rootUserTaskName, processInstance.getId(), john.getId());

        final ActivityInstance subStep = waitForUserTask(subProcUserTaskName, processInstance.getId());
        final ProcessInstance subProcInst = getProcessAPI().getProcessInstance(subStep.getParentProcessInstanceId());
        checkProcessDataInstance(dataName, subProcInst.getId(), dataValue);

        checkEvaluateExpression(subStep, dataName, String.class, dataValue);

        assignAndExecuteStep(subStep, john.getId());
        waitForProcessToFinish(subProcInst);
        waitForProcessToFinish(processInstance, TestStates.getAbortedState());

        disableAndDeleteProcess(process.getId());
    }

    private void checkEvaluateExpression(final ActivityInstance subStep, final String dataName, final Class<?> expressionType, final Serializable expectedValue)
            throws InvalidExpressionException, ExpressionEvaluationException {
        final Map<Expression, Map<String, Serializable>> expressions = new HashMap<Expression, Map<String, Serializable>>(1);
        final Expression contVarExpr = new ExpressionBuilder().createDataExpression(dataName, expressionType.getName());
        expressions.put(contVarExpr, null);

        final Map<String, Serializable> expressionResults = getProcessAPI().evaluateExpressionsOnActivityInstance(subStep.getId(), expressions);
        assertEquals(expectedValue, expressionResults.get(contVarExpr.getName()));
    }

    private void checkRetrieveDataInstances(final ProcessInstance processInstance, final ActivityInstance subStep, final ProcessInstance subProcInst)
            throws DataNotFoundException {
        checkProcessDataInstance("count", subProcInst.getId(), 1);
        checkProcessDataInstance("content", subProcInst.getId(), "childVar");
        checkProcessDataInstance("value", subProcInst.getId(), 10.0);
        checkProcessDataInstance("content", processInstance.getId(), "parentVar");
        checkActivityDataInstance("content", subStep.getId(), "childActivityVar");
    }

    private void checkProcessDataInstance(final String dataName, final long processInstanceId, final Serializable expectedValue) throws DataNotFoundException {
        final DataInstance processDataInstance;
        processDataInstance = getProcessAPI().getProcessDataInstance(dataName, processInstanceId);
        assertEquals(expectedValue, processDataInstance.getValue());
    }

    private void checkActivityDataInstance(final String dataName, final long activityInstanceId, final Serializable expectedValue) throws DataNotFoundException {
        final DataInstance activityDataInstance;
        activityDataInstance = getProcessAPI().getActivityDataInstance(dataName, activityInstanceId);
        assertEquals(expectedValue, activityDataInstance.getValue());
    }

    @Cover(classes = { SubProcessDefinition.class }, concept = BPMNConcept.EVENT_SUBPROCESS, keywords = { "event sub-process", "error", "call activity" }, jira = "ENGINE-536")
    @Test
    public void testErrorEventSubProcInsideTargetCallActivity() throws Exception {
        final String errorCode = "e1";
        final String subProcStartEventName = "errorStart";
        final ProcessDefinition targetProcess = deployAndEnableProcessWithErrorEventSubProcess(errorCode, errorCode, subProcStartEventName);
        final ProcessDefinition callerProcess = deployAndEnableProcessWithCallActivity("ProcessWithCallActivity", targetProcess.getName(),
                targetProcess.getVersion());
        final ProcessInstance processInstance = getProcessAPI().startProcess(callerProcess.getId());
        final ActivityInstance step1 = waitForUserTask("step1", processInstance.getId());
        waitForUserTaskAndExecuteIt("step2", processInstance.getId(), john.getId());

        final ActivityInstance subStep = waitForUserTask("subStep", processInstance.getId());
        final ProcessInstance calledProcInst = getProcessAPI().getProcessInstance(step1.getParentProcessInstanceId());
        final ProcessInstance subProcInst = getProcessAPI().getProcessInstance(subStep.getParentProcessInstanceId());

        waitForArchivedActivity(step1.getId(), TestStates.getAbortedState());
        assignAndExecuteStep(subStep, john.getId());
        waitForProcessToFinish(subProcInst);
        waitForProcessToFinish(calledProcInst, TestStates.getAbortedState());

        waitForUserTaskAndExecuteIt("step2", processInstance.getId(), john.getId());
        waitForProcessToFinish(processInstance);

        disableAndDeleteProcess(callerProcess.getId());
        disableAndDeleteProcess(targetProcess.getId());
    }
}
