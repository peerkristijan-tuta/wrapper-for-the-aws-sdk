import SdkClients.AwsClient;
import java.util.*;

class AwsClientDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter your access key ID: ");
        String accessKeyId = scanner.nextLine();

        System.out.print("Enter your secret access key: ");
        String secretAccessKey = scanner.nextLine();

        System.out.println("Logging in...");
        AwsClient client = AwsClient.login(accessKeyId, secretAccessKey);
        ArrayList<String> instanceIds = new ArrayList<String>();
        
        while (true) {
            if (instanceIds.size() == 0)
                System.out.println("No instances found.");
            else
                for (String instanceId : instanceIds.toArray(new String[0]))
                    System.out.println(instanceId + " " + client.getInstanceInfo(instanceId)[0] + " " + client.getInstanceInfo(instanceId)[1] + " " + client.getInstanceInfo(instanceId)[2]);

            System.out.print("\n1. Create instance\n2. Terminate instance\n3. Exit\nChoice: ");
            switch(scanner.nextLine()) {
                case "1":
                    System.out.print("Choose instance build\n1. Windows Server 4GB RAM\n2. Amazon Linux 2GB RAM\nChoice: ");
                    switch(scanner.nextLine()) {
                        case "1":
                            System.out.println("Creating instance...");
                            instanceIds.add(client.createInstance("resolve:ssm:/aws/service/ami-windows-latest/Windows_Server-2022-English-Full-Base", "t3.medium"));
                            break;
                        default:
                            System.out.println("Creating instance...");
                            instanceIds.add(client.createInstance("resolve:ssm:/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64", "t3.small"));
                    }
                    System.out.println("Instance created.");
                    break;
                case "2":
                    System.out.println("Choose instance to terminate");
                    int count = 1;
                    for (String instanceId : instanceIds.toArray(new String[0]))
                        System.out.println((count++) + ". " + instanceId + " " + client.getInstanceInfo(instanceId)[0] + " " + client.getInstanceInfo(instanceId)[1] + " " + client.getInstanceInfo(instanceId)[2] + "VCPU");
                    System.out.print("Choice: ");
                    System.out.println("Terminating instance " + instanceIds.get(Integer.valueOf(scanner.nextLine())-1));
                    client.terminateInstance(instanceIds.get(Integer.valueOf(scanner.nextLine())-1));
                    System.out.println("Instance terminated.");
                    break;
                default:
                    for (String instanceId : instanceIds.toArray(new String[0]))
                        if (!client.getInstanceInfo(instanceId)[1].equals("terminated")) {
                            System.out.println("Terminating instance " + instanceIds.get(Integer.valueOf(scanner.nextLine())-1));
                            client.terminateInstance(instanceId);
                        }
                    return;
            }
            System.out.println();
        }
    }
}