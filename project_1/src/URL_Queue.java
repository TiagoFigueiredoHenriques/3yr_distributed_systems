/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sd_project;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalTime;

class StartServer implements Runnable{
    int port;
    Thread t;
    List<String> urlArray;
    private final int MinutesBetweenSaves = 1;
    StartServer(int threadport, List<String> urls){
        port = threadport;
        t = new Thread(this);
        urlArray = urls;
        System.out.print("New Thread port: " + port+"\n");
        t.start();
    }
    
    public synchronized  void save(){
        try{
            synchronized(urlArray){
                FileOutputStream fileOut = new FileOutputStream("queue.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(urlArray);
                out.close();
                fileOut.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }    
    
    @Override
    public synchronized void run(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected "+port);
                
               
                try (
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        ) {
                    String inputLine;
                    
                    LocalTime currentTime = LocalTime.now();
                    LocalTime saveTime = currentTime.plusMinutes(MinutesBetweenSaves);
                    
                    while ((inputLine = in.readLine()) != null) {
                        
                        
                        synchronized(urlArray){
                            if (inputLine.equals("SEND")) {

                                String url = in.readLine();
                                urlArray.add(url);
                                //System.out.println("Port "+port+" Added link: " + url);


                            }else if(inputLine.equals("SEND 0")){
                                String url = in.readLine();
                                urlArray.add(0,url);
                                //System.out.println("Port "+port+" Added link: " + url);


                            }else if (inputLine.equals("RECEIVE")) {
                                if (urlArray.isEmpty()) {
                                    out.println("ERROR: Message buffer is empty");
                                } else {
                                    String url = urlArray.get(0);
                                    urlArray.remove(0);
                                    out.println(url);
                                    //System.out.println("Port: " + port +" Removed link: " + url);
                                }
                            } else {
                                out.println("ERROR: Invalid command");
                            }
                            
                            
                        }
                        
                        currentTime = LocalTime.now();
                        if(currentTime.compareTo(saveTime) >= 0){
                            save();
                            saveTime = currentTime.plusMinutes(MinutesBetweenSaves);
                        }
                    }

                    System.out.println("Client disconnected");
                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }    
        System.out.println(port + " exiting.");
    }
}
public class URL_Queue{
    private static final int PORT1 = 1234;
    private static final int PORT2 = 1235;
    private static final int PORT3 = 1236;
    private List<String> urlArray ;
    
    public URL_Queue() {
        readfile();
    }
    public List<String> getUrlArray() {
        return urlArray;
    }
    
    public final void readfile(){
        try{
            FileInputStream fileIn = new FileInputStream("queue.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            urlArray = Collections.synchronizedList((List<String>) in.readObject());
            in.close(); 
            fileIn.close();
            
        }catch (FileNotFoundException e) {
            urlArray = Collections.synchronizedList(new ArrayList<>()); 
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public static void main(String args[]) {
        URL_Queue urlQueue = new URL_Queue();
        new StartServer(PORT1, urlQueue.getUrlArray()); // create threads
        new StartServer(PORT2, urlQueue.getUrlArray());
        new StartServer(PORT3, urlQueue.getUrlArray());
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }
        System.out.println("Main thread exiting...");
    }
} 