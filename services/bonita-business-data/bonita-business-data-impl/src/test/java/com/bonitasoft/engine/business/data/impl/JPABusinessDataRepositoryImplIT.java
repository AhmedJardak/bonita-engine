package com.bonitasoft.engine.business.data.impl;

import static com.bonitasoft.pojo.EmployeeBuilder.anEmployee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.bonitasoft.engine.dependency.DependencyService;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import bitronix.tm.TransactionManagerServices;

import com.bonitasoft.engine.business.data.NonUniqueResultException;
import com.bonitasoft.engine.business.data.SBusinessDataNotFoundException;
import com.bonitasoft.pojo.Employee;
import com.bonitasoft.pojo.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testContext.xml" })
public class JPABusinessDataRepositoryImplIT {

    private JPABusinessDataRepositoryImpl businessDataRepository;

    @Autowired
    @Qualifier("businessDataDataSource")
    private DataSource datasource;

    @Autowired
    @Qualifier("notManagedBizDataSource")
    private DataSource modelDatasource;

    @Resource(name = "jpa-configuration")
    private Map<String, Object> configuration;

    @Resource(name = "jpa-model-configuration")
    private Map<String, Object> modelConfiguration;

    private JdbcTemplate jdbcTemplate;

    private UserTransaction ut;

    private EntityManager entityManager;

    @BeforeClass
    public static void initializeBitronix() throws NamingException, SQLException {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "bitronix.tm.jndi.BitronixInitialContextFactory");
        TransactionManagerServices.getConfiguration().setJournal(null);
    }

    @AfterClass
    public static void shutdownTransactionManager() {
        TransactionManagerServices.getTransactionManager().shutdown();
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(datasource);
        }

        final SchemaManager schemaManager = new SchemaManager(modelConfiguration, mock(TechnicalLoggerService.class));
        final BusinessDataModelRepositoryImpl businessDataModelRepositoryImpl = spy(new BusinessDataModelRepositoryImpl(mock(DependencyService.class),
                schemaManager, null, null));
        businessDataRepository = spy(new JPABusinessDataRepositoryImpl(businessDataModelRepositoryImpl, configuration));
        doReturn(true).when(businessDataModelRepositoryImpl).isDBMDeployed();
        ut = TransactionManagerServices.getTransactionManager();
        ut.begin();

        final Set<String> classNames = new HashSet<String>();
        classNames.add(Employee.class.getName());
        classNames.add(Person.class.getName());

        businessDataModelRepositoryImpl.update(classNames);
        businessDataRepository.start();
        entityManager = businessDataRepository.getEntityManager();
    }

    @After
    public void tearDown() throws Exception {
        ut.rollback();
        businessDataRepository.stop();

        final JdbcTemplate jdbcTemplate = new JdbcTemplate(modelDatasource);
        try {
            jdbcTemplate.update("drop table Employee");
            jdbcTemplate.update("drop table Person");
        } catch (final Exception e) {
            // ignore drop of non-existing table
        }
    }

    private Employee addEmployeeToRepository(final Employee employee) throws SBusinessDataNotFoundException {
        return entityManager.merge(employee);
    }

    @Test(expected = SBusinessDataNotFoundException.class)
    public void throwAnExceptionIfTheIdentifierIsNull() throws Exception {
        businessDataRepository.findById(Employee.class, null);
    }

    @Test
    public void findAnEmployeeByPrimaryKey() throws Exception {
        Employee expectedEmployee = anEmployee().build();
        expectedEmployee = addEmployeeToRepository(expectedEmployee);

        final Employee employee = businessDataRepository.findById(Employee.class, expectedEmployee.getPersistenceId());

        assertThat(employee).isEqualTo(expectedEmployee);
    }

    @Test(expected = SBusinessDataNotFoundException.class)
    public void throwExceptionWhenEmployeeNotFound() throws Exception {
        businessDataRepository.findById(Employee.class, -145l);
    }

    @Test
    public void persistNewEmployeeShouldAddEmployeeInRepository() throws Exception {
        final Employee employee = businessDataRepository.merge(anEmployee().build());

        final Employee myEmployee = businessDataRepository.findById(Employee.class, employee.getPersistenceId());
        assertThat(myEmployee).isEqualTo(employee);
    }

    @Test
    public void persistANullEmployeeShouldDoNothing() throws Exception {
        businessDataRepository.merge(null);

        final Long count = businessDataRepository.find(Long.class, "SELECT COUNT(*) FROM Employee e", null);
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void findListShouldAcceptParameterizedQuery() throws Exception {
        final String firstName = "anyName";
        Employee expectedEmployee = anEmployee().withFirstName(firstName).build();
        expectedEmployee = addEmployeeToRepository(expectedEmployee);

        final Map<String, Serializable> parameters = Collections.singletonMap("firstName", (Serializable) firstName);
        final Employee matti = businessDataRepository.find(Employee.class, "FROM Employee e WHERE e.firstName = :firstName", parameters);

        assertThat(matti).isEqualTo(expectedEmployee);
    }

    @Test(expected = NonUniqueResultException.class)
    public void findShouldThrowExceptionWhenSeveralResultsMatch() throws Exception {
        final String lastName = "Kangaroo";
        addEmployeeToRepository(anEmployee().withLastName(lastName).build());
        addEmployeeToRepository(anEmployee().withLastName(lastName).build());

        final Map<String, Serializable> parameters = Collections.singletonMap("lastName", (Serializable) lastName);
        businessDataRepository.find(Employee.class, "FROM Employee e WHERE e.lastName = :lastName", parameters);
    }

    @Test
    public void returnNullnWhenFindingAnUnknownEmployee() throws Exception {
        final Map<String, Serializable> parameters = Collections.singletonMap("lastName", (Serializable) "Unknown_lastName");
        assertThat(businessDataRepository.find(Employee.class, "FROM Employee e WHERE e.lastName = :lastName", parameters)).isNull();
    }

    @Test(expected = IllegalStateException.class)
    public void throwExceptionWhenUsingBDRWihtoutStartingIt() throws Exception {
        businessDataRepository.stop();

        businessDataRepository.findById(Employee.class, 124L);

        businessDataRepository.start();
    }

    @Test
    public void entityClassNames_is_an_empty_set_if_bdr_is_not_started() throws Exception {
        businessDataRepository.stop();

        final Set<String> classNames = businessDataRepository.getEntityClassNames();

        assertThat(classNames).isEmpty();
    }

    @Test
    public void updateTwoFieldsInSameTransactionShouldModifySameObject() throws Exception {
        Employee originalEmployee = addEmployeeToRepository(anEmployee().build());

        originalEmployee.setLastName("NewLastName");
        originalEmployee = businessDataRepository.merge(originalEmployee);
        originalEmployee.setFirstName("NewFirstName");
        businessDataRepository.merge(originalEmployee);

        final Employee updatedEmployee = businessDataRepository.findById(Employee.class, originalEmployee.getPersistenceId());
        assertThat(updatedEmployee).isEqualTo(originalEmployee);
    }

    @Test
    public void getEntityClassNames_should_return_the_classes_managed_by_the_bdr() throws Exception {
        final Set<String> classNames = businessDataRepository.getEntityClassNames();

        assertThat(classNames).containsExactly(Employee.class.getName(), Person.class.getName());
    }

    @Test(expected = SBusinessDataNotFoundException.class)
    public void aRemovedEntityShouldNotBeRetrievableAnyLonger() throws Exception {
        Employee employee = null;
        try {
            employee = addEmployeeToRepository(anEmployee().build());

            businessDataRepository.remove(employee);
        } catch (final Exception e) {
            fail("Should not fail here");
        }
        businessDataRepository.findById(Employee.class, employee.getPersistenceId());
    }

    @Test
    public void remove_should_not_throw_an_exception_with_a_null_entity() throws Exception {
        businessDataRepository.remove(null);
    }

    @Test
    public void remove_should_not_throw_an_exception_with_an_unknown_entity_without_an_id() throws Exception {
        businessDataRepository.remove(anEmployee().build());
    }

    @Test
    public void remove_should_not_throw_an_exception_with_an_unknown_entity() throws Exception {
        final Employee newEmployee = addEmployeeToRepository(anEmployee().build());
        businessDataRepository.remove(newEmployee);
        businessDataRepository.remove(newEmployee);
    }

    @Test
    public void findList_should_return_employee_list() throws Exception {
        final Employee e1 = addEmployeeToRepository(anEmployee().withFirstName("Hannu").withLastName("balou").build());
        final Employee e2 = addEmployeeToRepository(anEmployee().withFirstName("Aliz").withLastName("akkinen").build());
        final Employee e3 = addEmployeeToRepository(anEmployee().withFirstName("Jean-Luc").withLastName("akkinen").build());

        final List<Employee> employees = businessDataRepository.findList(Employee.class, "SELECT e FROM Employee e ORDER BY e.lastName ASC, e.firstName ASC",
                null, 0, 10);

        assertThat(employees).containsExactly(e2, e3, e1);
    }

    @Test
    public void findListShouldReturnEmptyListIfNoResults() throws Exception {
        final Map<String, Serializable> parameters = Collections.singletonMap("firstName", (Serializable) "Jaakko");
        final List<Employee> employees = businessDataRepository.findList(Employee.class,
                "SELECT e FROM Employee e WHERE e.firstName=:firstName ORDER BY e.lastName, e.firstName", parameters, 0, 10);
        assertThat(employees).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void findListShouldThrowAnExceptionIfAtLeastOneQueryParameterIsNotSet() throws Exception {
        businessDataRepository.findList(Employee.class, "SELECT e FROM Employee e WHERE e.firstName=:firstName ORDER BY e.lastName, e.firstName", null, 0, 10);
    }

    @Test
    public void findBasedOnAMultipleAttributeShouldReturnTheEntity() throws Exception {
        final Person person = new Person();
        person.setNickNames(Arrays.asList("John", "James", "Jack"));
        final Person expected = entityManager.merge(person);

        final Person actual = businessDataRepository.find(Person.class, "SELECT p FROM Person p WHERE 'James' IN ELEMENTS(p.nickNames)", null);
        assertThat(actual).isEqualTo(expected);

        actual.removeFrom("James");

        entityManager.merge(actual);

        final Person actual2 = businessDataRepository.find(Person.class, "SELECT p FROM Person p WHERE 'James' IN ELEMENTS(p.nickNames)", null);
        assertThat(actual2).isNull();
    }

}
