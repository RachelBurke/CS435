import java.io.*;
import java.net.*;

class rdtPacket {

    //Variables
    private String message;
    private int checksum;
    private int seqno; 
    private String packetMessage;

    rdtPacket (String message, int checksum, int seqno)
    {
        this.message = message;
        this.checksum = checksum;
        this.seqno = seqno;
        this.packetMessage = message + "|" + Integer.toString(checksum) + "|" + Integer.toString(seqno);
    }

    String getMessage()
    {
        return message;
    }

    int getChecksum()
    {
        return checksum;
    }

    int getSeqno()
    {
        return seqno;
    }

    String getPacketMessge()
    {
        return packetMessage;
    }

    void setMessage(String message)
    {
        this.message = message;
    }

    void setChecksum(int checksum)
    {
        this.checksum = checksum;
    }

    void setSeqno(int seqno)
    {
        this.seqno = seqno;
    }
}

class RDTClient {
    private static DatagramSocket clientSocket;
    private static InetAddress IPAddress;
    private static final int serverPort = 9876;

    public static void main(String args[]) throws Exception {
        try {
            String serverHostname = new String("127.0.0.1");

            if (args.length > 0)
                serverHostname = args[0];
            
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            clientSocket = new DatagramSocket();

            IPAddress = InetAddress.getByName(serverHostname);
            System.out.println("Attemping to connect to " + IPAddress + ") via UDP port " + serverPort);

            System.out.print("Enter Message: ");
            String message = inFromUser.readLine();

            //Ensuring a message was given
            while(message.equals(""))
            {
                System.out.print("Blank Message. Please Enter Message: ");
                message = inFromUser.readLine();
            }

            String response = "";
            int seqno = 0;
             while(response.equals(""))
             {
                rdt_send(message, seqno);
                response = rdt_rcv();
                if(seqno == 0)
                    seqno = 1;
                else 
                    seqno = 0;
             }

             System.out.println("Received response: " + response);
            clientSocket.close();
        } catch (UnknownHostException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private static String make_pkt(int seqno, String message, int checksum) {
        rdtPacket rdtPacket = new rdtPacket(message, checksum, seqno);
        System.out.println(rdtPacket.getPacketMessge());
        return rdtPacket.getPacketMessge();
    }

    private static void rdt_send(String message, int seqno) {
        System.out.println("[RDTClient:rdt_send] " + message);
        int checksum = compute_checksum(message);
        String packet = make_pkt(seqno, message, checksum);
        udt_send(packet);
    }

    private static void udt_send(String message) {
        try {
            byte[] sendData = new byte[1024];
            sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort);
            clientSocket.setSoTimeout(10000);
            clientSocket.send(sendPacket);
            System.out.println("[RDTClient:udt_send] Message sent");
        } catch (IOException ioe) {
            System.out.println("Failed to send");
        }
    }

    private static String rdt_rcv() {
        try {
            clientSocket.setSoTimeout(10000);
            try {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String message = new String(receivePacket.getData());
                IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                //Split Message
                String [] m = message.split("\\|");
                char seqno = m[2].charAt(0);
                rdtPacket rdtPacket;
                try {
                    rdtPacket = new rdtPacket(m[0], Integer.valueOf(m[1]), Integer.valueOf(seqno));
                
                    if(!(isCorrupt(rdtPacket)) && isACK(rdtPacket))
                    {
                        System.out.println("[RDTClient:rdt_rcv] Received packet from: " + IPAddress + ":" + port);
                        return message;
                    }
                } catch (NumberFormatException nfe)
                {
                    System.out.println("Incorrectly formatted.");
                }

            } catch (IOException ioe) {
                System.out.println("Failed to read from port");
            }
        } catch (SocketException se ) {
            System.out.println("Timeout.");
        }
        return "";
    }

    public static int compute_checksum(String message) {
            int checksum = 0;
            char [] m = message.toCharArray();
            for (int i = 0; i < m.length; i++)
                checksum += (int) m[i];
            checksum = ~checksum;
                
        return checksum;
    }

    public static boolean isCorrupt(rdtPacket packet) {
        String message = packet.getMessage();
        int checksum = packet.getChecksum();

        int message_checksum = compute_checksum(message);
        if(checksum == message_checksum)
            return false;

        return true;
    }

    public static boolean isACK(rdtPacket packet) {
        String message = packet.getMessage();
        if(message.equals("ACK"))
            return true;

        return false;
    }

   // Handled in main by sending other sequence number
   //public static boolean inOrder(rdtPacket packet, int seqno) {
    //} // has_seq()

}