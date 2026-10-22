package SdkClients;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.waiters.Ec2Waiter;
import software.amazon.awssdk.core.waiters.WaiterResponse;

public class AwsClient {
    Ec2Client ec2 = null;

    AwsClient(Ec2Client ec2) {
        this.ec2 = ec2;
    }

    public static AwsClient login(String accessKeyId, String secretAccessKey) {
        return new AwsClient(
            Ec2Client.builder()
                .region(Region.of("ap-southeast-5"))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                ))
                .build()
        );
    }

    public String createInstance(String imageId, String type) {
        RunInstancesResponse response = ec2.runInstances(r -> r
            .imageId(imageId)
            .instanceType(InstanceType.fromValue(type))
            .minCount(1)
            .maxCount(1)
        );

        ec2.waiter().waitUntilInstanceRunning(r -> r
            .filters(Filter.builder()
                .name("instance-id")
                .values(response.instances().get(0).instanceId())
                .build())
        );

        return response.instances().get(0).instanceId();
    }

    public WaiterResponse<DescribeInstancesResponse> terminateInstance(String instanceId) {
        ec2.terminateInstances(r -> r.instanceIds(instanceId));

        return ec2.waiter().waitUntilInstanceTerminated(r -> r
            .filters(Filter.builder()
                .name("instance-id")
                .values(instanceId)
                .build()
            )
        );
    }

    public String[] getInstanceInfo(String instanceId) {
        Instance instance = ec2.describeInstances(r -> r.instanceIds(instanceId)).reservations().get(0).instances().get(0);

        return new String[] {
            instance.platformAsString() != null ? instance.platformAsString() : "Linux",
            instance.state().nameAsString(),
            ec2.describeInstanceTypes(r -> r.instanceTypes(InstanceType.fromValue(instance.instanceTypeAsString()))).instanceTypes().get(0).memoryInfo().sizeInMiB() / 1024 + "GB"
        };
    }
}