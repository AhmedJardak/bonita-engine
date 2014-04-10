package com.bonitasoft.engine.log.api.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.queriablelogger.model.SQueriableLogSeverity;
import org.junit.Before;
import org.junit.Test;

public class MapQueriableLoggerStrategyTest {

    /**
     * 
     */
    private static final String DISABLE_LOGS_KEY = "org.bonitasoft.engine.services.queryablelog.disable";

    private Map<String, Boolean> map;

    @Before
    public void setUp() throws Exception {
        System.clearProperty(DISABLE_LOGS_KEY);
        map = new HashMap<String, Boolean>();
        map.put("action1:INTERNAL", true);
        map.put("action2:INTERNAL", false);
    }

    @Test
    public void isLoggable_should_return_false_if_propery_queryablelog_disable_is_set() throws Exception {
        // given
        System.setProperty(DISABLE_LOGS_KEY, "true");
        MapQueriableLoggerStrategy strategy = new MapQueriableLoggerStrategy(null);

        // when
        boolean loggable = strategy.isLoggable("any", SQueriableLogSeverity.INTERNAL);

        // then
        assertThat(loggable).isFalse();
    }

    @Test
    public void isLoggable_shoud_return_true_when_value_in_map_is_true() throws Exception {
        // given
        MapQueriableLoggerStrategy strategy = new MapQueriableLoggerStrategy(map);

        // when
        boolean loggable = strategy.isLoggable("action1", SQueriableLogSeverity.INTERNAL);

        // then
        assertThat(loggable).isTrue();
    }

    @Test
    public void isLoggable_shoud_return_false_when_value_in_map_is_false() throws Exception {
        // given
        MapQueriableLoggerStrategy strategy = new MapQueriableLoggerStrategy(map);

        // when
        boolean loggable = strategy.isLoggable("action2", SQueriableLogSeverity.INTERNAL);

        // then
        assertThat(loggable).isFalse();
    }

    @Test
    public void isLoggable_shoud_throw_exception_if_map_doesnt_contain_key() throws Exception {
        // given
        MapQueriableLoggerStrategy strategy = new MapQueriableLoggerStrategy(map);
        String actionType = "unknowAction";

        try {
            // when
            strategy.isLoggable(actionType, SQueriableLogSeverity.INTERNAL);
            fail("exception must be thrown");
        } catch (RuntimeException e) {
            // then
            assertThat(e.getMessage()).isEqualTo(
                    "The action type '" + actionType + "' with the severity '" + SQueriableLogSeverity.INTERNAL.name() + "' is not known as loggable.");
        }
    }

}
