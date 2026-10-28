package com.demo;

import wrappers.AwsClient;
import java.util.HashMap;
import java.util.Scanner;
import java.net.*;
import java.io.IOException;

public class Demo {
    static String converter(byte[] bytes) {
        String text = "";

        for (int x = 0; x < bytes.length; x++)
            text += (char) bytes[x];

        return text;
    }
    
    public static void main(String[] args) throws InterruptedException, IOException {
        AwsClient client = AwsClient.login(args[0], args[1]);

        String ec2Id = client.createEC2("/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-arm64", "t4g.small");
        Thread.sleep(10000);
        client.associateEc2WithIaM(ec2Id, args[2]);

        System.out.println("Allowing time for EC2 startup and SSM check-in...");
        while (true) {
            if (client.checkSSMCheckInStatus(ec2Id) && client.checkEC2InitializationStatus(ec2Id))
                break;
            Thread.sleep(5000);
        }
        System.out.println("EC2 startup and SSM check-in complete.");

        String s3BucketName = client.createS3("demo-bucket-6019");
        client.uploadToS3(s3BucketName, "Ec2Demo.java");

        System.out.println("EC2 Instance Actions:");
        System.out.println("Creating S3 Bucket");
        client.ssmToEc2(ec2Id, "aws s3 cp s3://demo-bucket-6019/Ec2Demo.java .", true);
        System.out.println("Installing Java");
        client.ssmToEc2(ec2Id, "sudo dnf install -y java-21-amazon-corretto-devel", true);
        
        System.out.println("Installing TailScale");
        client.ssmToEc2(ec2Id, "sudo dnf install -y dnf-plugins-core && sudo dnf config-manager --add-repo https://pkgs.tailscale.com/stable/fedora/tailscale.repo && sudo dnf install -y tailscale", true);
        client.ssmToEc2(ec2Id, "sudo systemctl start tailscaled", true);
        Thread.sleep(3000);
        client.ssmToEc2(ec2Id, "sudo tailscale up --authkey=" + args[3], true);

        System.out.println("Coompiling and running Java app");
        client.ssmToEc2(ec2Id, "javac Ec2Demo.java", true);
        client.ssmToEc2(ec2Id, "java Ec2Demo.java " + args[4], false);

        System.out.println("Waiting for ec2 instance..");
        Scanner scanner = new Scanner(System.in);

        DatagramSocket socket = new DatagramSocket(8000);
        socket.setSoTimeout(3000);
        byte[] confirmation = new byte[7];
        DatagramPacket packet = new DatagramPacket(confirmation, 7);

        while(true) {
            try {
                socket.receive(packet);
                System.out.println(converter(packet.getData()));
                break; 
            } catch (SocketTimeoutException exc) {
                System.out.print("Enter y to continue searching and n to exit program: ");
                
                if (!scanner.nextLine().equals("y"))
                    break;
            }
        }
        System.out.println("Proceeding to terminate EC2 and delete S3 bucket");
        client.terminateEc2(ec2Id);
        client.deleteS3(s3BucketName);
    }
}