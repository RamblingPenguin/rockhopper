package com.ramblingpenguin.rockhopper.ec2;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.lang.reflect.Field;

public class Ec2InstanceResourceFactory implements ResourceFactory<Ec2Instance, Instance> {

    private final Ec2Client client;

    public Ec2InstanceResourceFactory(Ec2Client client) {
        this.client = client;
    }

    @Override
    public Class<Ec2Instance> getAnnotationType() {
        return Ec2Instance.class;
    }

    @Override
    public Class<Instance> targetType() {
        return Instance.class;
    }

    @Override
    public void create(Ec2Instance annotation, Field field, Object target) {
        // Look for existing instance with the same name tag if name is provided
        if (!annotation.name().isEmpty()) {
            DescribeInstancesResponse response = client.describeInstances(DescribeInstancesRequest.builder()
                    .filters(Filter.builder()
                            .name("tag:Name")
                            .values(annotation.name())
                            .build())
                    .build());
            
            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    if (instance.state().name() != InstanceStateName.TERMINATED && 
                        instance.state().name() != InstanceStateName.SHUTTING_DOWN) {
                        setField(field, target, instance);
                        return;
                    }
                }
            }
        }

        // Create new instance
        RunInstancesRequest.Builder runInstancesBuilder = RunInstancesRequest.builder()
                .imageId(annotation.imageId())
                .instanceType(annotation.instanceType())
                .minCount(1)
                .maxCount(1);

        if (!annotation.name().isEmpty()) {
            runInstancesBuilder.tagSpecifications(TagSpecification.builder()
                    .resourceType(ResourceType.INSTANCE)
                    .tags(Tag.builder().key("Name").value(annotation.name()).build())
                    .build());
        }

        RunInstancesResponse runInstancesResponse = client.runInstances(runInstancesBuilder.build());
        Instance instance = runInstancesResponse.instances().get(0);

        setField(field, target, instance);
    }

    private void setField(Field field, Object target, Instance value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set field " + field.getName(), e);
        }
    }
}
