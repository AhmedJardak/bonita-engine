package com.bonitasoft.engine.reporting;

import com.bonitasoft.engine.CommonAPISPTest;
import com.bonitasoft.engine.api.PlatformAPI;
import com.bonitasoft.engine.api.PlatformAPIAccessor;
import com.bonitasoft.engine.platform.TenantCreator;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ArchivedProcessInstance;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.ExecutionException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.search.impl.SearchOptionsImpl;
import org.bonitasoft.engine.session.PlatformSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class ReportingAPIIT extends CommonAPISPTest {

    private static String lineSeparator = "\n";

    @Before
    public void setUp() throws BonitaException {
        login();
        getIdentityAPI().createUser("matti", "bpm", "Matti", "Mäkelä");
    }

    @After
    public void tearDown() throws BonitaException {
        getIdentityAPI().deleteUser("matti");
        logout();
    }

    @Test
    public void reportNumberOfUsers() throws BonitaException {
        final String csvUsers = getReportingAPI().selectList("SELECT COUNT(*) as nb FROM user_");
        assertTrue(("nb" + lineSeparator + "1" + lineSeparator).equalsIgnoreCase(csvUsers));
    }

    @Test
    public void reportUsers() throws BonitaException {
        final String csvUsers = getReportingAPI().selectList("SELECT userName, lastname FROM user_");
        assertEquals("USERNAME,LASTNAME" + lineSeparator + "matti,Mäkelä" + lineSeparator, csvUsers);
    }

    @Test
    public void reportUsersusingAlias() throws BonitaException {
        final String csvUsers = getReportingAPI().selectList("SELECT userName AS name, lastname FROM user_");
        assertEquals("NAME,LASTNAME" + lineSeparator + "matti,Mäkelä" + lineSeparator, csvUsers);
    }

    @Test
    public void searchReportsWithNoResults() throws BonitaException {
        final SearchOptions options = new SearchOptionsImpl(0, 10);
        SearchResult<Report> reports = getReportingAPI().searchReports(options);
        assertEquals(4, reports.getCount());
        final List<Report> r = reports.getResult();
        getReportingAPI().deleteReports(Arrays.asList(r.get(0).getId(), r.get(1).getId(), r.get(2).getId()));
        reports = getReportingAPI().searchReports(options);
        assertEquals(0, reports.getCount());
    }

    @Test
    public void checkSQLValidityOfProcessInstanceAverageTime() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("(CS.STARTDATE/86400000) as CS_START, ");
        builder.append("CS.ENDDATE AS CS_END_DATE, ");
        builder.append("(CS.ENDDATE/86400000) as CS_END, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("CS.SOURCEOBJECTID AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME, ");
        builder.append("( CS.ENDDATE - CS.STARTDATE ) AS CS_DURATION ");
        builder.append("FROM arch_process_instance  CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.ENDDATE > 0 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.ENDDATE BETWEEN 1369173600565 AND 1369864799565 ");
        builder.append("ORDER BY 14 DESC, 6, 4");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        assertTrue(("CS_PROCESS_DEFINITION_ID,CS_NAME,CS_STATE_ID,CS_START_DATE,CS_START,CS_END_DATE,CS_END,CS_ID,CS_SOURCEOBJECTID,APS_PROCESS_ID,APS_NAME,USR_FIRSTNAME,USR_LASTNAME,CS_DURATION" + lineSeparator)
                .equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfProcessInstancesInState() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("0 AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM process_instance CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.STATEID in (6) ");
        builder.append("AND CS.STARTDATE BETWEEN 1369173600470 AND 1369864799470 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("CS.SOURCEOBJECTID AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_process_instance CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.ENDDATE > 0 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.STATEID in (6) ");
        builder.append("AND CS.STARTDATE BETWEEN 1369173600470 AND 1369864799470 ");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        assertTrue(("CS_PROCESS_DEFINITION_ID,CS_NAME,CS_STATE_ID,CS_START_DATE,CS_ID,CS_SOURCEOBJECTID,APS_PROCESS_ID,APS_NAME,USR_FIRSTNAME,USR_LASTNAME" + lineSeparator)
                .equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfNumberOfProcessInstancesInState() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("CS_TABLE.CS_STATE_ID, ");
        builder.append("CS_TABLE.CS_START_DATE, ");
        builder.append("count(*) as CS_COUNT ");
        builder.append("FROM ");
        builder.append("( ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("(CS.STARTDATE/86400000) AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("null AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM process_instance CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.STATEID in (1) ");
        builder.append("AND CS.STARTDATE BETWEEN 1369173600170 AND 1369864799170 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("(CS.STARTDATE/86400000) AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("CS.SOURCEOBJECTID AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_process_instance  CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.ENDDATE > 0 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND CS.STATEID in (1) ");
        builder.append("AND CS.STARTDATE BETWEEN 1369173600170 AND 1369864799170 ");
        builder.append(") CS_TABLE ");
        builder.append("GROUP BY CS_TABLE.CS_STATE_ID, CS_TABLE.CS_START_DATE ");
        builder.append("ORDER BY 2, 1 ");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        assertTrue(("CS_STATE_ID,CS_START_DATE,CS_COUNT" + lineSeparator).equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfActivitiesInState() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("TSK.EXPECTEDENDDATE AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("'OPEN' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM flownode_instance TSK ");
        builder.append("INNER JOIN process_instance CS ON TSK.PARENTCONTAINERID = CS.ID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like 'completed' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600955 AND 1369864799955 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("TSK.EXPECTEDENDDATE AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("'OPEN' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_flownode_instance TSK ");
        builder.append("INNER JOIN process_instance  CS ON TSK.PARENTCONTAINERID = CS.ID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.STATEID = 2 ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like 'completed' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600955 AND 1369864799955 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("TSK.EXPECTEDENDDATE AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.SOURCEOBJECTID AS CS_ID, ");
        builder.append("'ARCHIVED' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_flownode_instance TSK ");
        builder.append("INNER JOIN arch_process_instance CS ON TSK.PARENTCONTAINERID = CS.SOURCEOBJECTID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.STATEID = 2 ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like 'completed' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600955 AND 1369864799955 ");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        assertTrue(("TSK_FLOW_NODE_DEFINITION_ID,TSK_DISPLAY_NAME,TSK_STATE_NAME,TSK_EXPECTED_END_DATE,CS_ID,CS_STATE_NAME,APS_PROCESS_ID,APS_NAME,USR_FIRSTNAME,USR_LASTNAME" + lineSeparator)
                .equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfListOfProcesses() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME ");
        builder.append("FROM process_definition APS ");
        builder.append("WHERE APS.PROCESSID = -1 ");
        builder.append("AND APS.TENANTID = 1");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        assertTrue(("APS_PROCESS_ID,APS_NAME" + lineSeparator).equalsIgnoreCase(csvUsers));
    }

    @Test
    public void checkSQLValidityOfNumberOfActivitiesInStateOpenArchvedAndFailed() throws ExecutionException {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("TSK_TABLE.TSK_STATE_NAME, ");
        builder.append("TSK_TABLE.TSK_EXPECTED_END_DATE, ");
        builder.append("count(*) as TSK_COUNT ");
        builder.append("FROM ");
        builder.append("( ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("(TSK.EXPECTEDENDDATE / 86400000 ) AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("'OPEN' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM flownode_instance TSK ");
        builder.append("INNER JOIN process_instance CS ON TSK.PARENTCONTAINERID = CS.ID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like '%' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600166 AND 1369864799166 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("(TSK.EXPECTEDENDDATE / 86400000 ) AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("'OPEN' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_flownode_instance TSK ");
        builder.append("INNER JOIN process_instance  CS ON TSK.PARENTCONTAINERID = CS.ID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.STATEID = 2 ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like '%' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600166 AND 1369864799166 ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("TSK.ID AS TSK_FLOW_NODE_DEFINITION_ID, ");
        builder.append("TSK.DISPLAYNAME AS TSK_DISPLAY_NAME, ");
        builder.append("TSK.STATENAME AS TSK_STATE_NAME, ");
        builder.append("(TSK.EXPECTEDENDDATE / 86400000 ) AS TSK_EXPECTED_END_DATE, ");
        builder.append("CS.SOURCEOBJECTID AS CS_ID, ");
        builder.append("'ARCHIVED' as CS_STATE_NAME, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_flownode_instance TSK ");
        builder.append("INNER JOIN arch_process_instance CS ON TSK.PARENTCONTAINERID = CS.SOURCEOBJECTID ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON TSK.ASSIGNEEID = USR.ID ");
        builder.append("WHERE TSK.KIND in ('manual','user') ");
        builder.append("AND TSK.STATEID = 2 ");
        builder.append("AND TSK.TENANTID = 1 ");
        builder.append("AND CS.TENANTID = 1 ");
        builder.append("AND APS.TENANTID = 1 ");
        builder.append("AND USR.TENANTID = 1 ");
        builder.append("AND TSK.STATENAME like '%' ");
        builder.append("AND TSK.EXPECTEDENDDATE BETWEEN 1369173600166 AND 1369864799166 ");
        builder.append(") TSK_TABLE ");
        builder.append("GROUP BY TSK_TABLE.TSK_STATE_NAME, TSK_TABLE.TSK_EXPECTED_END_DATE ");
        builder.append("ORDER BY 2, 1 ");

        final String csvUsers = getReportingAPI().selectList(builder.toString());
        assertTrue(("TSK_STATE_NAME,TSK_EXPECTED_END_DATE,TSK_COUNT" + lineSeparator).equalsIgnoreCase(csvUsers));
    }

    @Test
    public void addGetAndDeleteReport() throws BonitaException {
        final Report report = getReportingAPI().createReport("report1", null, null);
        assertEquals("report1", report.getName());
        assertFalse(report.isProvided());

        getReportingAPI().deleteReport(report.getId());
    }

    @Test
    public void addAndRetrieveReport() throws BonitaException {
        final Report report = getReportingAPI().createReport("addAndRetrieveReport_test", "a test report", null);
        final Report retrievedReport = getReportingAPI().getReport(report.getId());
        assertEquals(report, retrievedReport);

        getReportingAPI().deleteReport(retrievedReport.getId());
    }

    @Test(expected = AlreadyExistsException.class)
    // @Ignore("constraint violation problem for now... won't stay long.")
    public void addTwiceSameReportFails() throws BonitaException {
        final String reportName = "same_name";
        final Report report = getReportingAPI().createReport(reportName, "a test report", null);
        try {
            getReportingAPI().createReport(reportName, "another description", null);
        } finally {
            getReportingAPI().deleteReport(report.getId());
        }
    }

    @Test
    public void getReportContent() throws BonitaException {
        final byte[] reportContentBytes = "some dummy report content".getBytes();
        final Report report = getReportingAPI().createReport("getReportContent_test", "a test report with content", reportContentBytes);
        final byte[] retrievedReportContent = getReportingAPI().getReportContent(report.getId());
        assertTrue("Retrieved report content does not match the set content", Arrays.equals(reportContentBytes, retrievedReportContent));

        getReportingAPI().deleteReport(report.getId());
        try {
            getReportingAPI().getReportContent(report.getId());
            fail("Report content should have been deleted along with it.");
        } catch (final ReportNotFoundException e) {
            // ok.
        }
    }

    @Test
    public void searchProfiles() throws BonitaException {
        final Report report = getReportingAPI().createReport("report1", null, null);
        final SearchOptionsBuilder options = new SearchOptionsBuilder(0, 10);
        options.filter(ReportSearchDescriptor.NAME, "report1");
        final SearchResult<Report> searchReports = getReportingAPI().searchReports(options.done());
        assertEquals(1, searchReports.getCount());
        final Report report2 = searchReports.getResult().get(0);
        assertEquals(report, report2);

        getReportingAPI().deleteReports(Collections.singletonList(report.getId()));
    }

    @Test
    public void createTenantDeploysDefaultReports() throws BonitaException {
        logout();
        PlatformSession session = loginPlatform();
        PlatformAPI platformAPI = PlatformAPIAccessor.getPlatformAPI(session);
        final long tenantId = platformAPI.createTenant(new TenantCreator("newTenant", "a test tenant to check default report creation", "testIconName",
                "testIconPath", "myTenantAdmin", "theirPassword"));
        platformAPI.activateTenant(tenantId);
        logoutPlatform(session);
        loginWith("myTenantAdmin", "theirPassword", tenantId);
        try {
            final SearchOptions searchOptions = new SearchOptionsBuilder(0, 10).done();
            final SearchResult<Report> searchReports = getReportingAPI().searchReports(searchOptions);
            // 4 reports by default:
            assertEquals(4, searchReports.getCount());
        } finally {
            // cleanup:
            logout();
            session = loginPlatform();
            platformAPI = PlatformAPIAccessor.getPlatformAPI(session);
            platformAPI.deactiveTenant(tenantId);
            platformAPI.deleteTenant(tenantId);
            logoutPlatform(session);
            login();
        }
    }

    @Test
    public void getAllArchivedProcessInstances() throws Exception {
        final StringBuilder builder = new StringBuilder("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("0 AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM process_instance CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("AND APS.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("AND USR.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("$P!{__p_state_name} ");
        builder.append("AND CS.STARTDATE BETWEEN $P{_p_date_from} AND $P{_p_date_to} ");
        builder.append("$P!{_p_apps_id} ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("0 AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("'System' AS USR_FIRSTNAME, ");
        builder.append("'' AS USR_LASTNAME ");
        builder.append("FROM process_instance CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("WHERE CS.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("AND APS.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("$P!{__p_state_name} ");
        builder.append("AND CS.STARTDATE BETWEEN $P{_p_date_from} AND $P{_p_date_to} ");
        builder.append("AND CS.STARTEDBY = 0 ");
        builder.append("$P!{_p_apps_id} ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("CS.SOURCEOBJECTID AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("USR.FIRSTNAME AS USR_FIRSTNAME, ");
        builder.append("USR.LASTNAME AS USR_LASTNAME ");
        builder.append("FROM arch_process_instance  CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("INNER JOIN user_ USR ON CS.STARTEDBY = USR.ID ");
        builder.append("WHERE CS.ENDDATE > 0 ");
        builder.append("AND CS.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("AND APS.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("AND USR.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("$P!{__p_state_name} ");
        builder.append("AND CS.STARTDATE BETWEEN $P{_p_date_from} AND $P{_p_date_to} ");
        builder.append("$P!{_p_apps_id} ");
        builder.append("UNION ");
        builder.append("SELECT ");
        builder.append("CS.PROCESSDEFINITIONID AS CS_PROCESS_DEFINITION_ID, ");
        builder.append("CS.NAME AS CS_NAME, ");
        builder.append("CS.STATEID AS CS_STATE_ID, ");
        builder.append("CS.STARTDATE AS CS_START_DATE, ");
        builder.append("CS.ID AS CS_ID, ");
        builder.append("CS.SOURCEOBJECTID AS CS_SOURCEOBJECTID, ");
        builder.append("APS.PROCESSID AS APS_PROCESS_ID, ");
        builder.append("APS.NAME AS APS_NAME, ");
        builder.append("'System' AS USR_FIRSTNAME, ");
        builder.append("'' AS USR_LASTNAME ");
        builder.append("FROM arch_process_instance  CS ");
        builder.append("INNER JOIN process_definition APS ON CS.PROCESSDEFINITIONID = APS.PROCESSID ");
        builder.append("WHERE CS.ENDDATE > 0 ");
        builder.append("AND CS.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("AND APS.TENANTID = $P{BONITA_TENANT_ID} ");
        builder.append("AND CS.STARTEDBY = 0 ");
        builder.append("$P!{__p_state_name} ");
        builder.append("AND CS.STARTDATE BETWEEN $P{_p_date_from} AND $P{_p_date_to} ");

        String query = builder.toString();
        query = query.replace("$P{BONITA_TENANT_ID}", "1");
        query = query.replace("$P{_p_date_from}", "" + (System.currentTimeMillis() - 1000));
        query = query.replace("$P{_p_date_to}", "" + (System.currentTimeMillis() + 1000000));
        query = query.replace("$P!{__p_state_name}", "AND CS.STATEID in (1, 6) ");
        query = query.replace("$P!{_p_apps_id}", " ");

        final BusinessArchiveBuilder archiveBuilder = new BusinessArchiveBuilder();

        ProcessDefinitionBuilder processBuilder = new ProcessDefinitionBuilder();
        processBuilder.createNewInstance("SayGO", "1.0").addActor(ACTOR_NAME).addStartEvent("Start").addUserTask("step1", ACTOR_NAME).addEndEvent("End")
                .addSignalEventTrigger("GO").addTransition("Start", "step1").addTransition("step1", "End");
        archiveBuilder.createNewBusinessArchive().setProcessDefinition(processBuilder.done());
        final BusinessArchive endSignalArchive = archiveBuilder.done();

        processBuilder = new ProcessDefinitionBuilder();
        processBuilder.createNewInstance("GetGO", "1.0").addActor(ACTOR_NAME).addStartEvent("StartOnSignal").addSignalEventTrigger("GO")
                .addUserTask("Task1", ACTOR_NAME).addTransition("StartOnSignal", "Task1");
        archiveBuilder.createNewBusinessArchive().setProcessDefinition(processBuilder.done());
        final BusinessArchive startSignalArchive = archiveBuilder.done();

        final User john = createUser("john", "bpm");

        final ProcessDefinition processDefinitionWithStartSignal = deployAndEnableWithActor(startSignalArchive, ACTOR_NAME, john);
        final ProcessDefinition processDefinitionWithEndSignal = deployAndEnableWithActor(endSignalArchive, ACTOR_NAME, john);

        logout();
        loginWith("john", "bpm");

        // Check that the process with trigger signal on start is not started, before send signal
        final ProcessInstance processInstanceWithEndSignal = getProcessAPI().startProcess(processDefinitionWithEndSignal.getId());
        waitForUserTask("step1", processInstanceWithEndSignal);
        checkNbOfProcessInstances(1);

        List<HumanTaskInstance> taskInstances = getProcessAPI().getPendingHumanTaskInstances(john.getId(), 0, 10, ActivityInstanceCriterion.NAME_ASC);
        assertEquals(1, taskInstances.size());
        assertEquals("step1", taskInstances.get(0).getName());

        // Send signal
        assignAndExecuteStep(taskInstances.get(0), john.getId());
        waitForProcessToFinish(processInstanceWithEndSignal.getId());

        // Check that the process with trigger signal on start is started, after send signal
        waitForUserTask("Task1");

        String selectList = getReportingAPI().selectList(query);
        String[] split = selectList.split("\n");
        assertEquals(3, split.length);

        taskInstances = getProcessAPI().getPendingHumanTaskInstances(john.getId(), 0, 10, ActivityInstanceCriterion.NAME_ASC);
        assertEquals(1, taskInstances.size());
        assertEquals("Task1", taskInstances.get(0).getName());
        assignAndExecuteStep(taskInstances.get(0), john.getId());
        waitForProcessToFinish(taskInstances.get(0).getParentContainerId());

        final SearchOptionsBuilder searchOptionsBuilder = new SearchOptionsBuilder(0, 10);
        final SearchResult<ArchivedProcessInstance> search = getProcessAPI().searchArchivedProcessInstances(searchOptionsBuilder.done());
        assertEquals(2, search.getCount());

        selectList = getReportingAPI().selectList(query);
        split = selectList.split("\n");
        assertEquals(3, split.length);

        disableAndDeleteProcess(processDefinitionWithStartSignal);
        disableAndDeleteProcess(processDefinitionWithEndSignal);
        deleteUser(john);
    }

}
