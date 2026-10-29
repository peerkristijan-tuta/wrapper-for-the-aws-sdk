Wrapper Information


The wrapper is located in src/main/java/wrappers/AwsClient.java. The pom and demo files are not required by the AWSClient, which can be deployed in other projects as-is. The supported operations are:


- Launching and terminating EC2 instance and S3 bucket (if terminating an S3 bucket, it's automatically erased beforehand)
- Uploading files to an S3 bucket
- Sending SSM commands to an EC2 instance and deciding whether to stop until the command is finished or not
- Assigning an IAM instance profile to an EC2 instance
- Checking if an EC2 instance is fully running or if the SSM of an instance has successfully checked in with the AWS systems manager


A pre-requisite of using the wrapper is having an access key ID and secret access key for AWS SDK.


Demo Information

It's 5 var args are, in sequential order:

- The access key ID for AWS SDK
- The secret access key for AWS SDK
- The name of the IAM instance profile to associate with the one EC2 instance that the demo creates and runs (It's role must have the following permissions: AmazonSSMManagedInstanceCore and AmazonS3ReadOnlyAccess)
- A reusable Tailscale auth key
- The IP address in the Tailscale network that was assigned you by the organization


About TailScale

It's a service which allows computers from different LANs to send UDPs to each other over the internet by including them in its network and assigning them virtual IP addresses in that network. To get it up and running on your device to fulfill the requirements of the demo:


1. Create a TailScale account
2. Go to settings, then keys, then create authkey, and copy it
3. Go to your CLI, then enter sudo tailscale up --authkey=<authkey> (or its Windows equivalent) to get your device connected to TailScale
4. Then, after the sudo command has completed, enter tailscale ip to get the virtual ip address that was assigned to you
5. Enter tailscale down when you don't need it anymore 

