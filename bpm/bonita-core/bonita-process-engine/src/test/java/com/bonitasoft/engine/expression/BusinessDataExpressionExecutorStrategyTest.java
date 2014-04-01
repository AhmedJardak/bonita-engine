/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.expression;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.exceptions.SContext;
import org.bonitasoft.engine.core.expression.control.model.SExpressionContext;
import org.bonitasoft.engine.core.process.instance.api.FlowNodeInstanceService;
import org.bonitasoft.engine.core.process.instance.model.SFlowNodeInstance;
import org.bonitasoft.engine.data.instance.api.DataInstanceContainer;
import org.bonitasoft.engine.expression.ExpressionExecutorStrategy;
import org.bonitasoft.engine.expression.ExpressionType;
import org.bonitasoft.engine.expression.model.SExpression;
import org.bonitasoft.engine.expression.model.impl.SExpressionImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bonitasoft.engine.business.data.BusinessDataRepository;
import com.bonitasoft.engine.core.process.instance.api.RefBusinessDataService;
import com.bonitasoft.engine.core.process.instance.api.exceptions.SRefBusinessDataInstanceNotFoundException;
import com.bonitasoft.engine.core.process.instance.model.SRefBusinessDataInstance;

@RunWith(MockitoJUnitRunner.class)
public class BusinessDataExpressionExecutorStrategyTest {

    @Mock
    private BusinessDataRepository businessDataRepository;

    @Mock
    private RefBusinessDataService refBusinessDataService;

    @Mock
    private FlowNodeInstanceService flowNodeInstanceService;

    @InjectMocks
    private BusinessDataExpressionExecutorStrategy businessDataExpressionExecutorStrategy;

    private SFlowNodeInstance createAflowNodeInstanceInRepository() throws Exception {
        final SFlowNodeInstance flowNode = mock(SFlowNodeInstance.class);
        when(flowNode.getParentProcessInstanceId()).thenReturn(1234L);
        when(flowNode.getId()).thenReturn(456L);
        when(flowNodeInstanceService.getProcessInstanceId(456L, DataInstanceContainer.ACTIVITY_INSTANCE.name())).thenReturn(1234L);
        return flowNode;
    }

    private SimpleBizData createAbizDataInRepository() throws Exception {
        return createAbizDataInRepository(98L);
    }

    private SimpleBizData createAbizDataInRepository(final long bizDataId) throws Exception {
        final SimpleBizData bizData = new SimpleBizData(bizDataId);
        when(businessDataRepository.findById(SimpleBizData.class, bizData.getId())).thenReturn(bizData);
        return bizData;
    }

    private SRefBusinessDataInstance createARefBizDataInRepository(final SimpleBizData bizData, final long processInstanceId) throws Exception {
        return createARefBizDataInRepository(bizData, "bizDataName", processInstanceId);
    }

    private SRefBusinessDataInstance createARefBizDataInRepository(final SimpleBizData bizData, final String bizDataName, final long processInstanceId)
            throws Exception {
        final SRefBusinessDataInstance refBizData = mock(SRefBusinessDataInstance.class);
        when(refBizData.getDataClassName()).thenReturn(bizData.getClass().getName());
        when(refBizData.getName()).thenReturn(bizDataName);
        when(refBizData.getProcessInstanceId()).thenReturn(processInstanceId);
        when(refBizData.getDataId()).thenReturn(bizData.getId());
        when(refBusinessDataService.getRefBusinessDataInstance(refBizData.getName(), processInstanceId)).thenReturn(refBizData);
        return refBizData;
    }

    private HashMap<String, Object> buildBusinessDataExpressionContext(final long containerId, final DataInstanceContainer containerType) {
        final HashMap<String, Object> context = new HashMap<String, Object>();
        context.put(SExpressionContext.containerIdKey, containerId);
        context.put(SExpressionContext.containerTypeKey, containerType.name());
        return context;
    }

    private SExpressionImpl buildBusinessDataExpression(final String content) {
        final SExpressionImpl expression = new SExpressionImpl();
        expression.setContent(content);
        expression.setReturnType("com.bonitasoft.engine.expression.BusinessDataExpressionExecutorStrategyTest.LeaveRequest");
        expression.setExpressionType(ExpressionType.TYPE_BUSINESS_DATA.name());
        return expression;
    }

    @Test
    public void should_be_a_business_data_expression_kind_strategy() throws Exception {
        assertThat(businessDataExpressionExecutorStrategy.getExpressionKind()).isEqualTo(ExpressionExecutorStrategy.KIND_BUSINESS_DATA);
    }

    @Test
    public void evaluate_on_a_process_instance_should_return_biz_data_instance_corresponding_to_data_name_and_processInstance_id() throws Exception {
        final SimpleBizData expectedBizData = createAbizDataInRepository();
        final long proccessInstanceId = 1L;
        final SRefBusinessDataInstance refBizData = createARefBizDataInRepository(expectedBizData, proccessInstanceId);
        final HashMap<String, Object> context = buildBusinessDataExpressionContext(proccessInstanceId, DataInstanceContainer.PROCESS_INSTANCE);
        final SExpressionImpl buildBusinessDataExpression = buildBusinessDataExpression(refBizData.getName());
        when(flowNodeInstanceService.getProcessInstanceId(proccessInstanceId, DataInstanceContainer.PROCESS_INSTANCE.name())).thenReturn(proccessInstanceId);

        final Object fetchedBizData = businessDataExpressionExecutorStrategy.evaluate(buildBusinessDataExpression, context, null);

        assertThat(fetchedBizData).isEqualTo(expectedBizData);
    }

    @Test
    public void failingEvaluateShouldPutProcessInstanceIdInExceptionContext() throws Exception {
        final SimpleBizData expectedBizData = createAbizDataInRepository();
        final long proccessInstanceId = 1564L;
        final SRefBusinessDataInstance refBizData = createARefBizDataInRepository(expectedBizData, proccessInstanceId);
        final HashMap<String, Object> context = buildBusinessDataExpressionContext(proccessInstanceId, DataInstanceContainer.PROCESS_INSTANCE);
        final SExpressionImpl buildBusinessDataExpression = buildBusinessDataExpression(refBizData.getName());
        when(flowNodeInstanceService.getProcessInstanceId(proccessInstanceId, DataInstanceContainer.PROCESS_INSTANCE.name())).thenReturn(proccessInstanceId);
        doThrow(new SRefBusinessDataInstanceNotFoundException(444L, "toto")).when(refBusinessDataService).getRefBusinessDataInstance(anyString(), anyLong());

        try {
            businessDataExpressionExecutorStrategy.evaluate(buildBusinessDataExpression, context, null);
            fail("should throw Exception");
        } catch (SBonitaException e) {
            assertThat(((SBonitaException) e.getCause()).getContext().get(SContext.PROCESS_INSTANCE_ID)).isEqualTo(proccessInstanceId);
        }
    }

    @Test
    public void evaluate_on_a_task_instance_should_return_biz_data_instance_corresponding_to_data_name_and_processInstance_id() throws Exception {
        final SimpleBizData expectedBizData = createAbizDataInRepository();
        final SFlowNodeInstance flowNode = createAflowNodeInstanceInRepository();
        final SRefBusinessDataInstance refBizData = createARefBizDataInRepository(expectedBizData, flowNode.getParentProcessInstanceId());
        final HashMap<String, Object> context = buildBusinessDataExpressionContext(flowNode.getId(), DataInstanceContainer.ACTIVITY_INSTANCE);
        final SExpressionImpl buildBusinessDataExpression = buildBusinessDataExpression(refBizData.getName());

        final Object fetchedBizData = businessDataExpressionExecutorStrategy.evaluate(buildBusinessDataExpression, context, null);

        assertThat(fetchedBizData).isEqualTo(expectedBizData);
    }

    @Test
    public void evaluate_should_first_check_if_expression_already_evaluated_and_available_in_context() throws Exception {
        final HashMap<String, Object> context = new HashMap<String, Object>();
        final SimpleBizData expectedBizData = new SimpleBizData(12L);
        final String bizDataName = "businessDataName";
        context.put(bizDataName, expectedBizData);
        final SExpressionImpl buildBusinessDataExpression = buildBusinessDataExpression(bizDataName);

        final Object fetchedBizData = businessDataExpressionExecutorStrategy.evaluate(buildBusinessDataExpression, context, null);

        assertThat(fetchedBizData).isEqualTo(expectedBizData);
        verifyZeroInteractions(businessDataRepository);
        verifyZeroInteractions(refBusinessDataService);
        verifyZeroInteractions(flowNodeInstanceService);
    }

    @Test
    public void evaluate_should_resolve_multiple_expressions() throws Exception {
        final SimpleBizData firstBizData = createAbizDataInRepository(1L);
        final SimpleBizData secondBizData = createAbizDataInRepository(2L);
        final long processInstanceId = 6L;
        final SRefBusinessDataInstance firstRefBizData = createARefBizDataInRepository(firstBizData, "dataOne", processInstanceId);
        final SRefBusinessDataInstance secondRefBizData = createARefBizDataInRepository(secondBizData, "dataTwo", processInstanceId);
        final HashMap<String, Object> context = buildBusinessDataExpressionContext(processInstanceId, DataInstanceContainer.PROCESS_INSTANCE);
        final SExpression firstbuildBusinessDataExpression = buildBusinessDataExpression(firstRefBizData.getName());
        final SExpression secondbuildBusinessDataExpression = buildBusinessDataExpression(secondRefBizData.getName());
        when(flowNodeInstanceService.getProcessInstanceId(processInstanceId, DataInstanceContainer.PROCESS_INSTANCE.name())).thenReturn(processInstanceId);

        final List<Object> fetchedBizDatas = businessDataExpressionExecutorStrategy.evaluate(
                Arrays.asList(firstbuildBusinessDataExpression, secondbuildBusinessDataExpression), context, null);

        assertThat(fetchedBizDatas).contains(firstBizData, secondBizData);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void evaluate_should_not_resolve_expression_with_same_content_as_already_resolved_one() throws Exception {
        final SExpression firstbuildBusinessDataExpression = buildBusinessDataExpression("sameName");
        final SExpression secondbuildBusinessDataExpression = buildBusinessDataExpression("sameName");
        final BusinessDataExpressionExecutorStrategy strategy = spy(new BusinessDataExpressionExecutorStrategy(refBusinessDataService, businessDataRepository,
                flowNodeInstanceService));
        doReturn(new Object()).when(strategy).evaluate(any(SExpression.class), anyMap(), anyMap());

        strategy.evaluate(asList(firstbuildBusinessDataExpression, secondbuildBusinessDataExpression), null, null);

        verify(strategy, times(1)).evaluate(any(SExpression.class), anyMap(), anyMap());
    }

    @Test
    public void evaluation_result_should_be_pushed_in_context() throws Exception {
        assertThat(businessDataExpressionExecutorStrategy.mustPutEvaluatedExpressionInContext()).isEqualTo(true);
    }

}
