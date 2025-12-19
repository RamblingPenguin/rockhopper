package com.ramblingpenguin.rockhopper;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.util.Collection;

/**
 * A {@link TestEnvironment} that uses LocalStack via Testcontainers.
 */
public class LocalStackEnvironment implements TestEnvironment<LocalStackEnvironment> {

    private LocalStackContainer localStackContainer;

    @Override
    public void start() throws Exception {
        if (this.localStackContainer.isRunning()) {
            throw new IllegalStateException("LocalStackTestEnvironment has already been started");
        }
        this.localStackContainer.start();
    }

    @Override
    public void prepare() throws Exception {
        // Infrastructure is created directly by component preparers, no CDK needed
    }

    @Override
    public void initialize(Collection<InfrastructureComponent<LocalStackEnvironment>> components) throws Exception {
        this.localStackContainer = new LocalStackContainer(
                DockerImageName.parse("localstack/localstack:3.0"))
                .withServices(
                        components.stream()
                                .filter(c -> c instanceof LocalStackClientComponent)
                                .map(c -> (LocalStackClientComponent<?>) c)
                                .map(LocalStackClientComponent::getRequiredServices)
                                .flatMap(Collection::stream)
                                .distinct()
                                .toArray(LocalStackContainer.Service[]::new)
                );
    }

    @Override
    public void close() throws Exception {
        this.localStackContainer.close();
        this.localStackContainer = null;
    }

    public URI getEndpoint() {
        return this.localStackContainer.getEndpoint();
    }

    public String getAccessKey() {
        return this.localStackContainer.getAccessKey();
    }

    public String getSecretKey() {
        return this.localStackContainer.getSecretKey();
    }

    public String getRegion() {
        return this.localStackContainer.getRegion();
    }
}
