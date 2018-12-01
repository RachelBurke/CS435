import java.net.*;
import java.io.*;

public class ipcalc {

    public static void main(String[] args)
    {
        //Enter data using BufferReader 
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
         
        try
        {
            // Reading data using readLine 
            String cidr = reader.readLine(); 
       
            try
            {
                //Split the CIDR formatted network into IP address and Routing Prefix
                String [] cidrParts = cidr.split("/");
                //Get IP Address
                String ip = cidrParts[0];
                //Get Prefix
                String prefix = "";
                if (cidrParts.length < 2)
                    prefix = "0";
                else
                    prefix = cidrParts[1];
                
                if(Integer.parseInt(prefix) > 32)
                    throw new IndexOutOfBoundsException("Please enter a valid prefix.");

                //Calculate
                int mask = 0xffffffff << (32 - Integer.parseInt(prefix));
                byte[] bytes = new byte[]{(byte)(mask >>> 24), (byte)(mask >> 16 & 0xff), (byte)(mask >> 8 & 0xff), (byte)(mask & 0xff) };
                InetAddress netAddr = InetAddress.getByAddress(bytes);

                System.out.println("Net Address: " + netAddr.getHostAddress());

            }
            catch (IndexOutOfBoundsException e)
            {
                e.getMessage();
            }
        }
        catch (Exception e)
        {
            System.out.println("Unable to read input. Please enter valid CIDR format network as input.");
        }
    }
}
