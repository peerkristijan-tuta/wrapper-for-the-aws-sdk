import java.net.*;

class Ec2Demo {
    static byte[] converter(String text) {
        byte[] bytes = new byte[text.length()];

        for (int x = 0; x < text.length(); x++)
            bytes[x] = (byte) text.charAt(x);

        return bytes;
    }

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(8000);
        while(true)
            socket.send(
                new DatagramPacket(
                    converter("success"), "success".length(), 
                    InetAddress.getByName(args[0]), 8000
                )
            );
    }
}