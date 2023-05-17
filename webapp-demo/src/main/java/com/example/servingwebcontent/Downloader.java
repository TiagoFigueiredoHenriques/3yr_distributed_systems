package com.example.servingwebcontent;

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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


class NewThread extends Thread implements Runnable,Serializable {
    String ServerAdress;
    int port;
    Thread t;
    private final String MULTICAST_ADDRESS1,MULTICAST_ADDRESS2,MULTICAST_ADDRESS3;
    private int port_m1,port_m2,port_m3;
    
    NewThread(String SA, int p,String Multi_address1,int p_m1,String Multi_address2,int p_m2,String Multi_address3,int p_m3){
        super("Server " + (long) (Math.random() * 1000));
        ServerAdress = SA;
        port = p;
        port_m1 = p_m1;
        MULTICAST_ADDRESS1 = Multi_address1;
        port_m2 = p_m2;
        MULTICAST_ADDRESS2 = Multi_address2; 
        port_m3 = p_m3;
        MULTICAST_ADDRESS3 = Multi_address3; 
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
                MulticastSocket socketM1,socketM2,socketM3;
                System.out.println(this.getName() + " running...");
                try {
                    socketM1 = new MulticastSocket();
                    socketM2 = new MulticastSocket();
                    socketM3 = new MulticastSocket();

                    while(true){
                        out.println("RECEIVE");
                        final String url = in.readLine(); 

                        //System.out.println("Port " + port +" "+url);

                        if(!"ERROR: Message buffer is empty".equals(url) && url.charAt(0)=='h'){           
                           
                            try {
                                
                                Document doc = Jsoup.connect(url).get();
                                
                                Thread wordThread = new Thread(() -> {
                                    StringTokenizer tokens = new StringTokenizer(doc.text());
                                    while (tokens.hasMoreElements()){

                                        try {
                                            String message = "word"+" "+ url +" "+ tokens.nextToken().toLowerCase();
                                            byte[] buffer = message.getBytes();
                                            
                                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS1);
                                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port_m1);
                                            socketM1.send(packet);
                                        }  catch (IOException ex) {
                                            Logger.getLogger(NewThread.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });
                                
                                Thread urlInfoThread = new Thread (() -> {
                                    String title = doc.title();
                                    String quote;
                                    if (doc.body().text().length() < 200) {
                                        quote = doc.body().text(); // Use the entire body text if it's less than 200 characters
                                    } else {
                                        quote = doc.body().text().substring(0, 200); // Take the first 200 characters if it's more than or equal to 200 characters
                                    }
                                    int nConnections = (doc.select("a[href]")).size();
                                    //et<String> pointConnections = getConnections(url);

                                    UrlInfo newUrl = new UrlInfo(url, title, quote, nConnections, null);
                                    try {
                                            byte[] buffer = serializeObject(newUrl);
                                            
                                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS2);
                                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port_m2);
                                            socketM2.send(packet);
                                        }  catch (IOException ex) {
                                            Logger.getLogger(NewThread.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    
                                });
                                
                                Thread linkThread = new Thread(() -> {
                                    Elements links = doc.select("a[href]");
                                    for (Element link : links) {
                                        try {
                                            String linkUrl = link.attr("abs:href");
                                            out.println("SEND");
                                            out.println(linkUrl);
                                            
                                            String message = "link"+" "+url +" "+ linkUrl;
                                            byte[] buffer = message.getBytes();
                                            
                                            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS3);
                                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port_m3);
                                            socketM3.send(packet);
                                        } catch (IOException ex) {
                                            Logger.getLogger(NewThread.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });
                                
                                wordThread.start();
                                urlInfoThread.start();
                                linkThread.start();
                                
                                
                                wordThread.join();
                                urlInfoThread.join();
                                linkThread.join();
                                
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(NewThread.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private static byte[] serializeObject(UrlInfo urlInfo) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(urlInfo);
            return bos.toByteArray();
        }
    }
}
public class Downloader implements RemoteInterfaceD, Serializable {
    private static final int PORT1 = 1234;
    private static final int PORT2 = 1235;
    private static final String MULTICAST_ADDRESS1 = "224.3.2.1";
    private static final String MULTICAST_ADDRESS2 = "224.3.2.2";
    private static final String MULTICAST_ADDRESS3 = "224.3.2.3";
    private static final int PORT_M1 = 4321;
    private static final int PORT_M2 = 4322;
    private static final int PORT_M3 = 4323;
    private static final String ServerAdress = "localhost";
    
    public Downloader() {
    }

    @Override
    public boolean alive(){
        return true;
    }
    
    public static void main(String args[]) {
        try {
            Downloader d = new Downloader();
            Registry r1 = LocateRegistry.createRegistry(7010);
            r1.rebind("Downloader", d);
            new NewThread(ServerAdress,PORT1,MULTICAST_ADDRESS1,PORT_M1,MULTICAST_ADDRESS2,PORT_M2,MULTICAST_ADDRESS3,PORT_M3); // create threads
            new NewThread(ServerAdress,PORT2,MULTICAST_ADDRESS1,PORT_M1,MULTICAST_ADDRESS2,PORT_M2,MULTICAST_ADDRESS3,PORT_M3);
        } catch (RemoteException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
} 