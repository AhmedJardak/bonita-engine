/*******************************************************************************
 * Copyright (C) 2009, 2013 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.bar.InvalidBusinessArchiveFormatException;
import org.bonitasoft.engine.bpm.comment.Comment;
import org.bonitasoft.engine.bpm.comment.SearchCommentsDescriptor;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.InvalidProcessDefinitionException;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDeployException;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoCriterion;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.search.Order;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.session.PlatformSession;
import org.bonitasoft.engine.test.ClientEventUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bonitasoft.engine.CommonAPISPTest;
import com.bonitasoft.engine.SPBPMTestUtil;
import com.bonitasoft.engine.api.PlatformAPI;
import com.bonitasoft.engine.api.PlatformAPIAccessor;

public class SPProcessManagementTest extends CommonAPISPTest {

    @After
    public void afterTest() throws Exception {
        logout();
    }

    @Before
    public void beforeTest() throws Exception {
        login();
    }

    private void logoutThenloginAs(final String userName, final String password, final long tenantId) throws BonitaException {
        logout();
        loginWith(userName, password, tenantId);
    }

    @Test
    public void getProcessesListOnMultipleTenants() throws Exception {
        logout();
        final String tenantName = "myTestTenant";

        PlatformSession platformSession = loginPlatform();
        PlatformAPI platformAPI = PlatformAPIAccessor.getPlatformAPI(platformSession);
        final long tenantId = platformAPI.createTenant(new TenantCreator(tenantName, "default", "defaultIconName", "defaultIconPath", "default_tenant_name",
                "default_tenant_password"));
        platformAPI.activateTenant(tenantId);
        logoutPlatform(platformSession);

        login();

        assertEquals(0, getProcessAPI().getNumberOfProcessDeploymentInfos());
        List<Long> ids = createProcessDefinitionWithTwoHumanStepsAndDeployBusinessArchive(10);

        assertEquals(10, getProcessAPI().getNumberOfProcessDeploymentInfos());
        List<ProcessDeploymentInfo> processes = getProcessAPI().getProcessDeploymentInfos(0, 10, ProcessDeploymentInfoCriterion.NAME_DESC);
        assertEquals(10, processes.size());
        assertEquals(PROCESS_NAME + "09", processes.get(0).getName());
        assertEquals(PROCESS_NAME + "00", processes.get(9).getName());

        logoutThenloginAs("default_tenant_name", "default_tenant_password", tenantId);

        assertEquals(0, getProcessAPI().getNumberOfProcessDeploymentInfos());
        ids = createProcessDefinitionWithTwoHumanStepsAndDeployBusinessArchive(10);

        assertEquals(10, getProcessAPI().getNumberOfProcessDeploymentInfos());
        processes = getProcessAPI().getProcessDeploymentInfos(0, 10, ProcessDeploymentInfoCriterion.NAME_DESC);
        assertEquals(10, processes.size());
        assertEquals(PROCESS_NAME + "09", processes.get(0).getName());
        assertEquals(PROCESS_NAME + "00", processes.get(9).getName());
        getProcessAPI().deleteProcesses(ids);
        assertEquals(0, getProcessAPI().getNumberOfProcessDeploymentInfos());

        logoutThenlogin();
        ids = new ArrayList<Long>();
        assertNotSame(0, getProcessAPI().getNumberOfProcessDeploymentInfos());
        processes = getProcessAPI().getProcessDeploymentInfos(0, 10, ProcessDeploymentInfoCriterion.DEFAULT);
        for (final ProcessDeploymentInfo processDeploymentInfo : processes) {
            ids.add(processDeploymentInfo.getProcessId());
        }
        getProcessAPI().deleteProcesses(ids);
        assertEquals(0, getProcessAPI().getNumberOfProcessDeploymentInfos());

        logout();
        platformSession = loginPlatform();
        platformAPI = PlatformAPIAccessor.getPlatformAPI(platformSession);
        platformAPI.deactiveTenant(tenantId);
        platformAPI.deleteTenant(tenantId);
        logoutPlatform(platformSession);

        login();
    }

    @Test
    public void searchCommentsInTenants() throws Exception {
        final User user = createUser(USERNAME, PASSWORD);
        loginWith(USERNAME, PASSWORD);
        DesignProcessDefinition designProcessDefinition;
        designProcessDefinition = createProcessDefinitionWithHumanAndAutomaticSteps(Arrays.asList("step1", "step2"), Arrays.asList(true, true));
        final ProcessDefinition processDefinition = deployAndEnableWithActor(designProcessDefinition, ACTOR_NAME, user);
        final String commentContent1 = "commentContent1";
        final String commentContent2 = "commentContent2";
        final ProcessInstance processInstance = getProcessAPI().startProcess(processDefinition.getId());
        waitForUserTask("step1", processInstance);
        getProcessAPI().addProcessComment(processInstance.getId(), commentContent1);
        getProcessAPI().addProcessComment(processInstance.getId(), commentContent2);
        logout();

        final long tenant1 = SPBPMTestUtil.constructTenant("suomenlinna", null, null, "hamme", "saari");
        loginWith("hamme", "saari", tenant1);
        ClientEventUtil.deployCommand(getSession());
        final User user1 = createUser(USERNAME, PASSWORD);
        loginWith(USERNAME, PASSWORD, tenant1);
        final ProcessDefinition processDefinition1 = deployAndEnableWithActor(designProcessDefinition, ACTOR_NAME, user);
        final ProcessInstance processInstance1 = getProcessAPI().startProcess(processDefinition1.getId());
        waitForUserTask("step1", processInstance1);
        final String commentContent11 = "commentContent11";
        final String commentContent12 = "commentContent12";
        getProcessAPI().addProcessComment(processInstance1.getId(), commentContent11);
        getProcessAPI().addProcessComment(processInstance1.getId(), commentContent12);

        final SearchOptionsBuilder builder0 = new SearchOptionsBuilder(0, 5);
        builder0.filter(SearchCommentsDescriptor.PROCESS_INSTANCE_ID, processInstance1.getId());
        builder0.sort(SearchCommentsDescriptor.POSTDATE, Order.ASC);

        final SearchResult<Comment> searchResult0 = getProcessAPI().searchComments(builder0.done());
        final List<Comment> commentList0 = searchResult0.getResult();
        assertEquals(2, commentList0.size());
        disableAndDeleteProcess(processDefinition1);
        deleteUser(user1);
        ClientEventUtil.undeployCommand(getSession());
        logout();
        login();
        disableAndDeleteProcess(processDefinition);
        deleteUser(user);
        SPBPMTestUtil.destroyTenant(tenant1);
    }

    private List<Long> createProcessDefinitionWithTwoHumanStepsAndDeployBusinessArchive(final int nbProcess) throws InvalidProcessDefinitionException,
            ProcessDeployException, InvalidBusinessArchiveFormatException, AlreadyExistsException {
        final List<Long> ids = new ArrayList<Long>();
        for (int i = 0; i < nbProcess; i++) {
            String processName = PROCESS_NAME;
            if (i >= 0 && i < 10) {
                processName += "0";
            }
            final DesignProcessDefinition processDefinition = createProcessDefinitionWithHumanAndAutomaticSteps(processName + i, PROCESS_VERSION + i,
                    Arrays.asList("step1", "step2"), Arrays.asList(true, true));
            ids.add(getProcessAPI().deploy(new BusinessArchiveBuilder().createNewBusinessArchive().setProcessDefinition(processDefinition).done()).getId());
        }
        return ids;
    }

}
