import java.io.*;
import java.net.*;
import java.util.*;

class dnsClient {
    
    private static byte[] buildQuery(String domain) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // *** Build a DNS Request Frame ****
        // Identifier: A 16-bit identifier
        dos.writeShort(0x1111);

        // Write Query Flags - this is a Query
        dos.writeShort(0x0100);

        // Question Count: Specifies the number of questions in the Question section of the message.
        dos.writeShort(0x0001);

        // Answer Count: Specifies the number of answers in the Answer section of the message.
        dos.writeShort(0x0000);

        // NS Count
        dos.writeShort(0x0000);

        // AR Count
        dos.writeShort(0x0000);

        // Query Name
        String[] domainParts = domain.split("\\.");
        System.out.println(domain + " has " + domainParts.length + " parts");

        for (int i = 0; i<domainParts.length; i++) {
            System.out.println("Writing: " + domainParts[i]);
            byte[] domainBytes = domainParts[i].getBytes("UTF-8");
            dos.writeByte(domainBytes.length);
            dos.write(domainBytes);
        }

        // No more parts
        dos.writeByte(0x00);

        // Type 0x01 = A (Host Request)
        dos.writeShort(0x0001);

        // Class 0x01 = IN
        dos.writeShort(0x0001);

        byte[] dnsMessage = baos.toByteArray();

        return dnsMessage;
    }
    
    private static String readPacket(byte[] receivePacket) throws Exception
    {
        ByteArrayInputStream iaos = new ByteArrayInputStream(receivePacket);
        DataInputStream ios = new DataInputStream(iaos);

        // *** Read a DNS Request Frame ****
        // Identifier: A 16-bit identifier
        ios.readShort();

        // Write Query Flags - this is a Query
        ios.readShort();

        // Question Count: Specifies the number of questions in the Question section of the message.
        ios.readShort();

        // Answer Count: Specifies the number of answers in the Answer section of the message.
        short answerCount = ios.readShort();

        // Authority RRs Count
        ios.readShort();

        // Additional RRs Count
        ios.readShort();

        // Query Name
        int domainLength;
        while ((domainLength = ios.readByte()) > 0)
        {
            byte[] domainBytes = new byte[domainLength];

            for (int i = 0; i < domainLength; i++)
                domainBytes[i] = ios.readByte();
        }

        String address = "";
        for (int x = 0; x < answerCount; x++)
        {
            // Type
            ios.readShort();

            // Class
            ios.readShort();

            //Answer Name
            ios.readShort();

            //Answer Type
            ios.readShort();

            //Answer Class
            ios.readShort();

            //Answer TTL
            ios.readInt();

            //Answer Data Length
            short addressLength = ios.readShort();
    
            //Answer Address
            for (int i = 0; i < addressLength; i++){
                if(i == addressLength-1)
                    address = address + String.format("%d", (ios.readByte() & 0xFF)) + "\n";
                else
                    address = address + String.format("%d", (ios.readByte() & 0xFF)) + ".";
            }
        }
        return address;
    }

    public static void main(String args[]) throws Exception
    {

        Scanner scanner = new Scanner(System. in); 
        String domain = scanner.nextLine();

        // the DNS server to use as a name or IP address
        String serverHostname = new String("8.8.8.8");
        // create the client socket
        DatagramSocket clientSocket = new DatagramSocket();

        // print the details
        InetAddress IPAddress = InetAddress.getByName(serverHostname);
        System.out.println("Attemping to connect to " + IPAddress + ":53");

        //build the query
        byte[] dnsMessage = buildQuery(domain);

        // pack up the bytes and send them
        DatagramPacket sendPacket = new DatagramPacket(dnsMessage, dnsMessage.length, IPAddress, 53);
        clientSocket.send(sendPacket);

        // and wait for a response
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        System.out.println("Waiting for return packet");
        clientSocket.setSoTimeout(10000);

        try
        {
            clientSocket.receive(receivePacket);
            System.out.println("Packet Returned. Reading...");

            String address = readPacket(receivePacket.getData());
            System.out.println(address);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}