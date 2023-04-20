/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sd_project;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.StringTokenizer;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class NewThread extends Thread implements Runnable,Serializable {
    String ServerAdress;
    int port;
    Thread t;
    private String MULTICAST_ADDRESS;
    private int port_m;
    
    NewThread(String SA, int p,String Multi_address,int p_m){
        super("Server " + (long) (Math.random() * 1000));
        ServerAdress = SA;
        port = p;
        port_m = p_m;
        MULTICAST_ADDRESS = Multi_address; 
        t = new Thread(this);
        System.out.print("New Thread port: " + port + "\n");
        t.start();
    }
    
    @Override
    public void run(){
        
        while(true){
        
            try (Socket socket = new Socket(ServerAdress, port);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
                MulticastSocket socketM;
                System.out.println(this.getName() + " running...");
                try {
                    socketM = new MulticastSocket();

                    String url;

                    while(true){
                        out.println("RECEIVE");
                        url = in.readLine();

                        //System.out.println("Port " + port +" "+url);

                        if(!"ERROR: Message buffer is empty".equals(url)){           
                            try {
                                Document doc = Jsoup.connect(url).get();
                                StringTokenizer tokens = new StringTokenizer(doc.text());

                                while (tokens.hasMoreElements()){
                                    String message = "word"+" "+url +" "+ tokens.nextToken().toLowerCase();
                                    byte[] buffer = message.getBytes();

                                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port_m);
                                    socketM.send(packet);
                                }    
                                Elements links = doc.select("a[href]");
                                for (Element link : links) {
                                    String linkUrl = link.attr("abs:href");
                                    out.println("SEND");
                                    out.println(linkUrl);

                                    String message = "link"+" "+url +" "+ linkUrl;
                                    byte[] buffer = message.getBytes();

                                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port_m);
                                    socketM.send(packet);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else{
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(NewThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socket.close();
                }

            } catch (IOException e) {
                System.err.println("Error communicating with server: " + e.getMessage());
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(NewThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
public class Downloader{
    private static final int PORT1 = 1234;
    private static final int PORT2 = 1235;
    private static final String MULTICAST_ADDRESS = "224.3.2.1";
    private static final int PORT_M1 = 4321;
    private static final int PORT_M2 = 4321;
    private static final String ServerAdress = "localhost";
    
    public Downloader() {
    }
    
    public static void main(String args[]) {
        new NewThread(ServerAdress,PORT1,MULTICAST_ADDRESS,PORT_M1); // create threads
        new NewThread(ServerAdress,PORT2,MULTICAST_ADDRESS,PORT_M2);
    }
} 