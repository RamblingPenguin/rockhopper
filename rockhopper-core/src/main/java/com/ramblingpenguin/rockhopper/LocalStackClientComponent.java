package com.ramblingpenguin.rockhopper;

import org.testcontainers.containers.localstack.LocalStackContainer;

import java.util.EnumSet;

public interface LocalStackClientComponent<CLIENT> extends CloudClientComponent<CLIENT, LocalStackEnvironment> {
    EnumSet<LocalStackContainer.Service> getRequiredServices();
}
