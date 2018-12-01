import java.net.*;
import java.text.DateFormat;
import java.io.*;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.text.SimpleDateFormat;

class httpRequest
{
    //Variables
    private String method;
    private String path;
    private String protocol; 
    private String[] headers;

    //Constructor to parse the http request
    httpRequest (String message)
    {
        //Split up the message line by line
        String[] lines = message.split("\n");
        //Break down the parts of the first line because we need each individual piece
        String[] parts = lines[0].split(" ");
        //Parse the message for the HTTP Request
        //Get the method - Method should be GET (or PUT or POST or ... but not for the project requirements)
        method = parts[0];
        //Get the file path
        path = parts[1];
        //e.g. HTTP/1.1
        protocol = parts[2];
        //Get the header lines that follow the first line into a header string array
        headers = new String[lines.length-1];
        for (int i = 1; i < lines.length; i++)
            headers[i-1] = lines[i];
    }

    //get methods
    //Get the type of request
    String getMethod() { return method; }
    //Get the file path
    String getPath() { return path; }
    //Get the http protocol
    String getprotocol() { return protocol; }
    //Get the request headers
    String[] getHeaders() { return headers; }
}

 class httpResponse {
    //Variables
    String protocol;
    String statusCode;
    String phrase;
    String server;
    String date;
    String last_modified;
    String content_type;
    String content_length;
    String connection;
    String[] headers;
    String path;

    //Create an empty response
    httpResponse (httpRequest hreq)
    {
        //Tell the request
        System.out.println("HTTP Request:");
        System.out.println(hreq.getMethod() + " " + hreq.getPath() + " " + hreq.getprotocol());
        for (int i = 0; i < hreq.getHeaders().length; i++)
            System.out.println(hreq.getHeaders()[i]);
        System.out.println("\n\n");

        //Set variables
        protocol = "";
        statusCode = "";
        phrase = "";
        server = "";
        date = "";
        last_modified = "";
        content_type = "";
        content_length = "";
        connection = "";
    }

    //Make a response
    String response()
    {
        String response = protocol + " " + statusCode + " " + phrase + "\r\n";
        if (statusCode.equals("200"))
            response = response.concat("Date: " + date + "\r\n");
        response = response.concat("Server: " + server + "\r\n");
        if (statusCode.equals("200"))
            response = response.concat("Last-Modified: " +last_modified + "\r\n");
        if (statusCode.equals("200"))
            response = response.concat("Content-Length: " + content_length + "\r\n");
        if (statusCode.equals("200"))    
        response = response.concat("Content-Type: " + content_type + "\r\n");
        if (statusCode.equals("200"))
        response = response.concat("Connection: " + connection + "\r\n");
        response = response.concat("\r\n");

        return response;
    }

    //set methods
    //set the protocol
    public void setProtocol(String protocol) { this.protocol = protocol; }
    //set the Status Code
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    //set the phrase
    public void setPhrase(String phrase) { this.phrase = phrase; }
    //set the date
    public void setDate(String date) { this.date = date; }
    //set the server
    public void setServer(String server) { this.server = server; }
    //set the date last modified
    public void setLast_Modified(String last_modified) { this.last_modified = last_modified; }
    //set the content-type
    public void setContent_Type(String content_type) { this.content_type = content_type; }
    //set the content-length
    public void setContent_Length(String content_length) { this.content_length = content_length; }
    //set the connection
    public void setConnection(String connection) { this.connection = connection; }
    //set the path
    public void setPath(String path) { this.path = path; }
}

public class HTTPServer {

    //Port to listen into
    public final static int port = 8080;
    //Document Root
    public final static String ROOT = "./home/vagrant/html";
    //Documents for various cases
    static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "/404.html";
    static final String INTERNAL_ERROR = "/500.html";

    public static void main(String[] args)
    {
        ServerSocket serverSocket;
        Socket connection;

        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port: " + port);
            try
            {
                while (true)
                {
                    connection = serverSocket.accept();

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = null;
                    String request = new String();

                    while (((line = br.readLine()) != null) && (!(line.equals(""))))
                        request = request.concat(line + "\n");
                  
                    // parse the request
                    httpRequest hreq = new httpRequest(request);

                    //Initialize the Response for a Request
                    httpResponse hres = new httpResponse(hreq);

                    //Set up for file output stream
                    FileInputStream fis;
                    byte[] data;

                    if((!hreq.getMethod().equals("GET") || !(hreq.getprotocol().equals("HTTP/1.1") || hreq.getprotocol().equals("HTTP/1.0"))))
                    {
                        String path = ROOT + INTERNAL_ERROR;
                        File notF = new File(path);
                        fis = new FileInputStream(notF);
                        data = new byte[(int) notF.length()];
                        fis.read(data);
                        hres.setProtocol(hreq.getprotocol());
                        hres.setStatusCode("500");
                        hres.setPhrase("SERVER ERROR");
                        hres.setServer("Rachel's HTTP Server");

                        String[] filetype = path.split("\\.");
                        if (filetype[filetype.length - 1].equals("html") || filetype[filetype.length - 1].equals("css"))
                            hres.setContent_Type("text/" + filetype[filetype.length - 1]);
                        else if (filetype[filetype.length - 1].equals("jpeg") || filetype[filetype.length - 1].equals("jpg") || filetype[filetype.length - 1].equals("png"))
                            hres.setContent_Type("image/" + filetype[filetype.length - 1]);
                        hres.setContent_Length(Integer.toString(data.length));

                        hres.setPath(path); 
                        fis.close();
                    }
                    else
                    {
                        //locate the file
                        String file = ROOT + hreq.getPath();
                        if (hreq.getPath().equals("/"))
                        file = file.concat("index.html");

                        try
                        {
                            // Try to read the file
                            File f = new File(file);
                            fis = new FileInputStream(f);
                            data = new byte[(int) f.length()];
                            fis.read(data);
                            hres.setProtocol(hreq.getprotocol());
                            hres.setStatusCode("200");
                            hres.setPhrase("OK");
                            hres.setServer("Rachel's HTTP Server");

                            DateFormat dateFormat = new SimpleDateFormat("E, d MMM YYYY hh:mm:ss z"); 
                            hres.setDate(dateFormat.format(Calendar.getInstance().getTime()));

                            hres.setLast_Modified("Fri, 14 Sep 2018 14:03:04 GMT");

                            String[] filetype = file.split("\\.");
                            if (filetype[filetype.length - 1].equals("html") || filetype[filetype.length - 1].equals("css"))
                                hres.setContent_Type("text/" + filetype[filetype.length - 1]);
                            else if (filetype[filetype.length - 1].equals("jpeg") || filetype[filetype.length - 1].equals("jpg"))
                                hres.setContent_Type("image/" + filetype[filetype.length - 1]);

                            hres.setContent_Length(Integer.toString(data.length));
                            hres.setConnection("Closed");
                            hres.setPath(file);
                            fis.close();
                        }
                        catch(Exception e)
                        {
                            String path = ROOT + FILE_NOT_FOUND;

                            File notF = new File(path);
                            fis = new FileInputStream(notF);
                            data = new byte[(int) notF.length()];
                            fis.read(data);

                            hres.setProtocol(hreq.getprotocol());
                            hres.setStatusCode("404");
                            hres.setPhrase("FILE NOT FOUND");
                            hres.setServer("Rachel's HTTP Server");

                            String[] filetype = path.split("\\.");
                            if (filetype[filetype.length - 1].equals("html") || filetype[filetype.length - 1].equals("css"))
                                hres.setContent_Type("text/" + filetype[filetype.length - 1]);
                            else if (filetype[filetype.length - 1].equals("jpeg") || filetype[filetype.length - 1].equals("jpg"))
                                hres.setContent_Type("image/" + filetype[filetype.length - 1]);
                            hres.setContent_Length(Integer.toString(data.length));

                            hres.setPath(path);       
                            fis.close();        
                        }
                    }
                    connection.getOutputStream().write(hres.response().getBytes("UTF-8"));
                    connection.getOutputStream().write(data);
                    connection.close();
                }
            }
            catch (Exception e)
            {
                serverSocket.close();
                System.err.println(e);
            }
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
    }
}