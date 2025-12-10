package com.ramblingpenguin.rockhopper;

import java.util.Collection;

public interface TestEnvironment<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends AutoCloseable {

    void start() throws Exception;

    void prepare() throws Exception;

    void initialize(Collection<InfrastructureComponent<ENVIRONMENT>> components) throws Exception;
}
