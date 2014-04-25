package com.bonitasoft.engine.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.bonitasoft.engine.bpm.process.ActivationState;
import org.bonitasoft.engine.bpm.process.ConfigurationState;
import org.bonitasoft.engine.core.process.definition.model.impl.SProcessDefinitionDeployInfoImpl;
import org.bonitasoft.engine.core.process.instance.model.SStateCategory;
import org.bonitasoft.engine.core.process.instance.model.impl.SProcessInstanceImpl;
import org.bonitasoft.engine.core.process.instance.model.impl.SUserTaskInstanceImpl;
import org.bonitasoft.engine.identity.model.impl.SUserImpl;
import org.bonitasoft.engine.persistence.PersistentObject;
import org.bonitasoft.engine.persistence.TenantHibernatePersistenceService;
import org.bonitasoft.engine.services.PersistenceService;
import org.bonitasoft.engine.services.SPersistenceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

import com.bonitasoft.engine.CommonAPISPTest;
import com.bonitasoft.engine.core.reporting.ReportingService;
import com.bonitasoft.engine.service.impl.SpringTenantServiceAccessor;

public class TaskListIT extends CommonAPISPTest {

    private PersistenceService persistenceservice;

    private ReportingService reportingService;

    @Before
    public void setUp() {
        SpringTenantServiceAccessor accessor = new SpringTenantServiceAccessor(1L);
        persistenceservice = accessor.getBeanAccessor().getService(TenantHibernatePersistenceService.class);
        reportingService = accessor.getReportingService();
    }
    
    @After
    public void cleanUp() throws Exception {
        BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
        transactionManager.begin();
        persistenceservice.deleteByTenant(SProcessDefinitionDeployInfoImpl.class, null);
        persistenceservice.deleteByTenant(SProcessInstanceImpl.class, null);
        persistenceservice.deleteByTenant(SUserImpl.class, null);
        persistenceservice.deleteByTenant(SUserTaskInstanceImpl.class, null);
        transactionManager.commit();
    }

    @Test
    public void should_retrieve_only_not_deleted_tasks() throws Exception {
        SUserImpl user = createUser();
        SProcessDefinitionDeployInfoImpl processDef = createApps();
        SProcessInstanceImpl processInstance = createInstance(processDef);
        SUserTaskInstanceImpl deletedTask = createDeletedUserTask(1L, "shouldNotBeRetrieved", processInstance, user);
        SUserTaskInstanceImpl expectedTask = createUserTask(2L, "shouldBeRetrieved", processInstance, user);
        insert(processDef, processInstance, user, deletedTask, expectedTask);
        
        String csv = executeQuery(getTaskListQuery());
    
        assertThat(csv).contains(expectedTask.getDisplayName());
        assertThat(csv).doesNotContain(deletedTask.getDisplayName());
    }
    
    private String executeQuery(String query) throws Exception {
        BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
        transactionManager.begin();
        String csv = reportingService.selectList(query);
        transactionManager.commit();
        return csv;
    }
    
    private void insert(PersistentObject... persistentObject) throws Exception {
        BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
        transactionManager.begin();
        for (PersistentObject o : persistentObject) {
            persistenceservice.insert(o);
        }
        transactionManager.commit();
    }
    
    private String getTaskListQuery() throws IOException {
        String query = IOUtils.toString(TaskListIT.class.getResourceAsStream("TaskList.sql"));
        query = query.replace("$P{BONITA_TENANT_ID}", "1");
        query = query.replace("$P!{_p_state_name}", "");
        query = query.replace("$P{_p_date_from}", "1369173600470");
        query = query.replace("$P{_p_date_to}", String.valueOf(new Date().getTime()));
        query = query.replace("$P!{_p_apps_id}", "");
        return query;
    }
    
    private SUserTaskInstanceImpl createUserTask(long id, String displayName, SProcessInstanceImpl instance, SUserImpl user) {
        SUserTaskInstanceImpl tsk = new SUserTaskInstanceImpl("userTak", 1L, instance.getId(), 1L, 0, null, 0L, 0L);
        tsk.setId(id);
        tsk.setRootContainerId(instance.getId());
        tsk.setStateName("completed");
        tsk.setAssigneeId(user.getId());
        tsk.setReachedStateDate(new Date().getTime());
        tsk.setTenantId(1L);
        tsk.setDisplayName(displayName);
        tsk.setDeleted(false);
        return tsk;
    }
    
    private SUserTaskInstanceImpl createDeletedUserTask(long id, String displayName, SProcessInstanceImpl instance, SUserImpl user) {
        SUserTaskInstanceImpl tsk = createUserTask(id, displayName, instance, user);
        tsk.setDeleted(true);
        return tsk;
    }

    private SUserImpl createUser() {
        SUserImpl user = new SUserImpl();
        user.setId(1L);
        user.setFirstName("firstname");
        user.setLastName("lastName");
        user.setUserName("firstname.lastName");
        user.setTenantId(1L);
        return user;
    }

    private SProcessInstanceImpl createInstance(SProcessDefinitionDeployInfoImpl aps) throws SPersistenceException {
        SProcessInstanceImpl cs = new SProcessInstanceImpl("instance", aps.getProcessId());
        cs.setId(1L);
        cs.setTenantId(1L);
        cs.setStateCategory(SStateCategory.NORMAL);
        return cs;
    }

    private SProcessDefinitionDeployInfoImpl createApps() {
        SProcessDefinitionDeployInfoImpl aps = new SProcessDefinitionDeployInfoImpl();
        aps.setProcessId(1L);
        aps.setName("processDefName");
        aps.setId(1L);
        aps.setVersion("1.0");
        aps.setActivationState(ActivationState.ENABLED.name());
        aps.setConfigurationState(ConfigurationState.RESOLVED.name());
        aps.setTenantId(1L);
        return aps;
    }
}
