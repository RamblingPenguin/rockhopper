package com.ramblingpenguin.rockhopper.ec2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Instance;

@ExtendWith(LocalStackEC2Infrastructure.class)
public class LocalStackEC2InfrastructureTest {

    @Ec2Instance(imageId = "ami-00000000", name = "TestInstance")
    Instance instance;

    @Test
    public void testInstanceCreation(Ec2Client client) {
        Assertions.assertNotNull(instance);
        Assertions.assertEquals("ami-00000000", instance.imageId());
        Assertions.assertEquals("TestInstance", instance.tags().stream()
                .filter(t -> t.key().equals("Name"))
                .findFirst()
                .get()
                .value());
    }
}
