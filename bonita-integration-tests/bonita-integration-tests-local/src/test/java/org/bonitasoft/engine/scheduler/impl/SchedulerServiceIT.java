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
package org.bonitasoft.engine.scheduler.impl;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.bonitasoft.engine.scheduler.impl.JobThatMayThrowErrorOrJobException.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.bonitasoft.engine.bpm.CommonBPMServicesTest;
import org.bonitasoft.engine.builder.BuilderFactory;
import org.bonitasoft.engine.persistence.QueryOptions;
import org.bonitasoft.engine.util.FunctionalMatcher;
import org.bonitasoft.engine.scheduler.JobService;
import org.bonitasoft.engine.scheduler.SchedulerService;
import org.bonitasoft.engine.scheduler.builder.SJobDescriptorBuilderFactory;
import org.bonitasoft.engine.scheduler.builder.SJobParameterBuilderFactory;
import org.bonitasoft.engine.scheduler.job.ReleaseWaitersJob;
import org.bonitasoft.engine.scheduler.job.VariableStorage;
import org.bonitasoft.engine.scheduler.model.SFailedJob;
import org.bonitasoft.engine.scheduler.model.SJobDescriptor;
import org.bonitasoft.engine.scheduler.model.SJobParameter;
import org.bonitasoft.engine.scheduler.trigger.OneExecutionTrigger;
import org.bonitasoft.engine.scheduler.trigger.OneShotTrigger;
import org.bonitasoft.engine.scheduler.trigger.Trigger;
import org.bonitasoft.engine.scheduler.trigger.UnixCronTrigger;
import org.bonitasoft.engine.scheduler.trigger.UnixCronTriggerForTest;
import org.bonitasoft.engine.test.util.TestUtil;
import org.bonitasoft.engine.transaction.UserTransactionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchedulerServiceIT extends CommonBPMServicesTest {

    private SchedulerService schedulerService;
    private JobService jobService;
    private UserTransactionService userTransactionService;
    private final VariableStorage storage = VariableStorage.getInstance();

    @Before
    public void before() throws Exception {
        schedulerService = getTenantAccessor().getSchedulerService();
        userTransactionService = getTenantAccessor().getUserTransactionService();
        jobService = getTenantAccessor().getJobService();
        TestUtil.stopScheduler(schedulerService, getTransactionService());
        if (!schedulerService.isStarted()) {
            schedulerService.initializeScheduler();
            schedulerService.start();
        }
        getTenantAccessor().getSessionAccessor().setTenantId(getDefaultTenantId());
    }

    @After
    public void after() {
        storage.clear();
    }

    @Test
    public void canRestartTheSchedulerAfterShutdown() throws Exception {
        schedulerService.stop();
        assertTrue(schedulerService.isStopped());
        schedulerService.initializeScheduler();
        schedulerService.start();
        assertTrue(schedulerService.isStarted());
    }

    @Test
    public void doNotExecuteAFutureJob() throws Exception {
        final Date future = new Date(System.currentTimeMillis() + 10000000);
        final String variableName = "myVar";
        final SJobDescriptor jobDescriptor = BuilderFactory.get(SJobDescriptorBuilderFactory.class)
                .createNewInstance("org.bonitasoft.engine.scheduler.job.IncrementVariableJob", "IncrementVariableJob").done();
        final List<SJobParameter> parameters = new ArrayList<SJobParameter>();
        parameters.add(BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance("jobName", "testDoNotExecuteAFutureJob").done());
        parameters.add(BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance("variableName", variableName).done());
        parameters.add(BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance("throwExceptionAfterNIncrements", -1).done());
        final Trigger trigger = new OneExecutionTrigger("events", future, 10);
        getTransactionService().begin();
        schedulerService.schedule(jobDescriptor, parameters, trigger);
        getTransactionService().complete();
        Thread.sleep(200);
        assertNull(storage.getVariableValue(variableName));
    }

    @Test
    public void doNotThrowAnExceptionWhenDeletingAnUnknownJob() throws Exception {
        getTransactionService().begin();
        final boolean deleted = schedulerService.delete("MyJob");
        getTransactionService().complete();
        assertFalse(deleted);
    }

    /*
     * We must ensure that:
     * * pause only jobs of the current tenant
     * * trigger new job are not executed
     * * resume the jobs resume it really
     * *
     */
    @Test
    public void pause_and_resume_jobs_of_a_tenant() throws Exception {
        long tenantForJobTest1 = createTenant("tenantForJobTest1");
        long tenantForJobTest2 = createTenant("tenantForJobTest2");

        changeTenant(tenantForJobTest1);
        final String jobName = "ReleaseWaitersJob";
        Date now = new Date();
        SJobDescriptor jobDescriptor = BuilderFactory.get(SJobDescriptorBuilderFactory.class)
                .createNewInstance(ReleaseWaitersJob.class.getName(), jobName + "1").done();
        List<SJobParameter> parameters = new ArrayList<SJobParameter>();
        parameters.add(BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance("jobName", jobName).done());
        parameters.add(BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance("jobKey", "1").done());
        Trigger trigger = new UnixCronTriggerForTest("events", now, 10, "0/1 * * * * ?");

        // trigger it
        getTransactionService().begin();
        schedulerService.schedule(jobDescriptor, parameters, trigger);
        getTransactionService().complete();
        ReleaseWaitersJob.waitForJobToExecuteOnce();

        // pause
        getTransactionService().begin();
        schedulerService.pauseJobs(sessionAccessor.getTenantId());
        getTransactionService().complete();
        Thread.sleep(100);
        ReleaseWaitersJob.checkNotExecutedDuring(1500);

        // trigger the job in an other tenant
        changeTenant(tenantForJobTest2);
        now = new Date(System.currentTimeMillis() + 100);
        jobDescriptor = BuilderFactory.get(SJobDescriptorBuilderFactory.class).createNewInstance(ReleaseWaitersJob.class.getName(), jobName + "2").done();
        parameters = new ArrayList<>();
        parameters.add(BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance("jobName3", jobName).done());
        parameters.add(BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance("jobKey", "3").done());
        trigger = new OneShotTrigger("events3", now, 10);
        getTransactionService().begin();
        schedulerService.schedule(jobDescriptor, parameters, trigger);
        getTransactionService().complete();
        ReleaseWaitersJob.waitForJobToExecuteOnce();
    }

    @Test
    public void should_be_able_to_list_job_that_failed_because_of_an_Error() throws Exception {
        // schedule a job that throws an Error
        schedule(jobDescriptor(JobThatMayThrowErrorOrJobException.class, "MyJob"),
                new OneShotTrigger("triggerJob", new Date(System.currentTimeMillis() + 100)),
                singletonMap(TYPE, ERROR));

        //we have failed job
        List<SFailedJob> failedJobs = await().until(() -> inTx(() -> jobService.getFailedJobs(0, 100)), hasSize(1));
        assertThat(failedJobs).hasOnlyOneElementSatisfying(f ->
                assertThat(f.getLastMessage()).contains("an Error"));
    }

    @Test
    public void should_be_able_to_restart_a_job_that_failed_because_of_a_SJobExecutionException() throws Exception {
        // schedule a job that throws a SJobExecutionException
        schedule(jobDescriptor(JobThatMayThrowErrorOrJobException.class, "MyJob"),
                new OneShotTrigger("triggerJob", new Date(System.currentTimeMillis() + 100)),
                singletonMap(TYPE, JOBEXCEPTION));
        SJobDescriptor persistedJobDescriptor = getFirstPersistedJob();

        //we have failed job
        List<SFailedJob> failedJobs = await().until(() -> inTx(() -> jobService.getFailedJobs(0, 100)), hasSize(1));
        assertThat(failedJobs).hasOnlyOneElementSatisfying(f ->
                assertThat(f.getLastMessage()).contains("a Job exception"));

        //reschedule the job: no more exception
        inTx(() -> {
            schedulerService.retryJobThatFailed(persistedJobDescriptor.getId(), toJobParameterList(singletonMap(TYPE, NO_EXCEPTION)));
            return null;
        });
        await().until(() -> storage.getVariableValue("nbSuccess", 0).equals(1));
    }

    @Test
    public void should_be_able_to_restart_a_cron_job_that_failed_because_of_a_SJobExecutionException() throws Exception {
        // schedule a job that throws a SJobExecutionException
        schedule(jobDescriptor(JobThatMayThrowErrorOrJobException.class, "MyJob"),
                new UnixCronTrigger("triggerJob", new Date(System.currentTimeMillis() + 100), "* * * * * ?"),
                singletonMap(TYPE, JOBEXCEPTION));
        SJobDescriptor persistedJobDescriptor = getFirstPersistedJob();

        //ensure there is more than one failure: i.e. cron is still triggering new jobs
        await().until(() -> storage.getVariableValue("nbJobException", 0), isGreaterThan(1));

        List<SFailedJob> sFailedJobs = inTx(() -> jobService.getFailedJobs(0, 100));

        assertThat(sFailedJobs).hasSize(1);
        //ensure we trace the number of failure
        assertThat(sFailedJobs.get(0).getNumberOfFailures()).isGreaterThan(1);


        //reschedule the job: no more exception
        inTx(() -> {
            schedulerService.retryJobThatFailed(persistedJobDescriptor.getId(), toJobParameterList(singletonMap(TYPE, NO_EXCEPTION)));
            return null;
        });

        //ensure there is more than one success: i.e. cron is still triggering new jobs
        await().until(() -> storage.getVariableValue("nbSuccess", 0), isGreaterThan(1));
        //ensure no more failed job is present
        assertThat(inTx(() -> jobService.getFailedJobs(0, 100))).isEmpty();
    }


    @Test
    public void should_keep_a_failed_job_when_failing_once() throws Exception {
        // schedule a job that throws a SJobExecutionException
        schedule(jobDescriptor(JobThatMayThrowErrorOrJobException.class, "MyJob"),
                new UnixCronTrigger("triggerJob", new Date(System.currentTimeMillis() + 100), "* * * * * ?"),
                singletonMap(TYPE, FAIL_ONCE));
        SJobDescriptor persistedJobDescriptor = getFirstPersistedJob();

        //this job fail only the first time
        await().until(() -> storage.getVariableValue("nbJobException", 0), isGreaterThan(0));
        await().until(() -> storage.getVariableValue("nbSuccess", 0), isGreaterThan(0));

        List<SFailedJob> sFailedJobs = inTx(() -> jobService.getFailedJobs(0, 100));

        assertThat(sFailedJobs).hasSize(1);
        //ensure we trace the number of failure
        assertThat(sFailedJobs.get(0).getNumberOfFailures()).isEqualTo(1);


        //reschedule the job: no more exception
        inTx(() -> {
            schedulerService.retryJobThatFailed(persistedJobDescriptor.getId(), toJobParameterList(singletonMap(TYPE, NO_EXCEPTION)));
            return null;
        });

        //ensure there is more than one success: i.e. cron is still triggering new jobs
        await().until(() -> storage.getVariableValue("nbSuccess", 0), isGreaterThan(1));
        //ensure no more failed job is present
        assertThat(inTx(() -> jobService.getFailedJobs(0, 100))).isEmpty();
    }

    private FunctionalMatcher<Integer> isGreaterThan(int i) {
        return t -> t > i;
    }

    private SJobDescriptor getFirstPersistedJob() throws Exception {
        return inTx(() -> jobService.searchJobDescriptors(new QueryOptions(0, 1))).get(0);
    }

    private <T> T inTx(Callable<T> callable) throws Exception {

        return userTransactionService.executeInTransaction(() -> {
            getTenantAccessor().getSessionAccessor().setTenantId(getDefaultTenantId());
            return callable.call();
        });
    }


    private SJobDescriptor jobDescriptor(Class<?> jobClass, String jobName) {
        return BuilderFactory.get(SJobDescriptorBuilderFactory.class)
                .createNewInstance(jobClass.getName(), jobName).done();
    }

    private void schedule(SJobDescriptor jobDescriptor, Trigger trigger, Map<String, Serializable> parameters) throws Exception {
        List<SJobParameter> parametersList = toJobParameterList(parameters);
        inTx(() -> {
            schedulerService.schedule(jobDescriptor, parametersList, trigger);
            return null;
        });
    }

    private List<SJobParameter> toJobParameterList(Map<String, Serializable> parameters) {
        return parameters.entrySet().stream().map(e -> BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance(e.getKey(), e.getValue()).done()).collect(Collectors.toList());
    }

}