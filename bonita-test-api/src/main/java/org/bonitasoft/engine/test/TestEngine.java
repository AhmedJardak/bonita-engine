package org.bonitasoft.engine.test;

import org.bonitasoft.engine.BonitaDatabaseConfiguration;

/**
 * @author Baptiste Mesta
 */
public interface TestEngine {
    String TECHNICAL_USER_NAME = "install";
    String TECHNICAL_USER_PASSWORD = "install";

    boolean start() throws Exception;

    void stop() throws Exception;

    void clearData() throws Exception;

    void setDropOnStart(boolean dropOnStart);

    void setDropOnStop(boolean dropOnStop);

    void setBonitaDatabaseProperties(BonitaDatabaseConfiguration database);

    void setBusinessDataDatabaseProperties(BonitaDatabaseConfiguration database);
}
