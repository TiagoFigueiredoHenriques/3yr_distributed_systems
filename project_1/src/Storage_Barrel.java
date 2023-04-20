/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sd_project;

/**
 *
 * @author Miguel_Fazenda
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;

import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalTime;


/*
A classe Storage_Barrel guarda e destriubui as informacoes conseguidas pelos downloaders, estes transmitem essa informacao atraves do processo multicast.
A classe tambem esta ligada por RMI atraves da RemoteInterfaceSB, permitindo a esta utilizar alguns dos seus metedos
*/
public class Storage_Barrel extends Thread implements RemoteInterfaceSB,Serializable{
    private String MULTICAST_ADDRESS = "224.3.2.1";
    private int PORT;
    private String name;
    private int MinutesBetweenSaves = 1;
    
    private Map<String, Set<String>> index;
    
    private Map<String, Set<String>> urlConnections;
    
    public Storage_Barrel(String name,int port_m) {
        super("Server " + (long) (Math.random() * 1000));
        PORT = port_m; 
        readFile();
        this.name = name;
        
        System.out.println("Server " + name);
        
        
    }
    
    /*
    A funcao getMapFromLargerFile le os ficheiros realizados e compara qual o maior, atribuindo esse ficheiro ao objeto correspondente, de modo a que no caso de fail de um dos barrels, esse 
    nao permaneca com informacao desatualizada
    */
    public final HashMap<String, Set<String>> getMapFromLargerFile(String filename1, String filename2, String filename3) {
        FileInputStream fileIn1 = null;
        FileInputStream fileIn2 = null;
        FileInputStream fileIn3 = null;

        HashMap<String, Set<String>> result = null;

        try {
            fileIn1 = new FileInputStream(filename1);
            fileIn2 = new FileInputStream(filename2);
            fileIn3 = new FileInputStream(filename3);

            int fileSize1 = fileIn1.available();
            int fileSize2 = fileIn2.available();
            int fileSize3 = fileIn3.available();

            if (fileSize1 > fileSize2) {
                if (fileSize1 > fileSize3) {
                    ObjectInputStream in = new ObjectInputStream(fileIn1);
                    result = (HashMap<String, Set<String>>) in.readObject();
                    in.close();
                } else {
                    ObjectInputStream in = new ObjectInputStream(fileIn3);
                    result = (HashMap<String, Set<String>>) in.readObject();
                    in.close();
                }
            } else {
                if (fileSize2 > fileSize3) {
                    ObjectInputStream in = new ObjectInputStream(fileIn2);
                    result = (HashMap<String, Set<String>>) in.readObject();
                    in.close();
                } else {
                    ObjectInputStream in = new ObjectInputStream(fileIn3);
                    result = (HashMap<String, Set<String>>) in.readObject();
                    in.close();
                }
            }

            return  result;
        }catch (FileNotFoundException e) {
            result = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileIn1 != null) {
                    fileIn1.close();
                }
                if (fileIn2 != null) {
                    fileIn2.close();
                }
                if (fileIn3 != null) {
                    fileIn3.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    
    /*
    A funcao escolhe quais os ficheiros que devem ser lidos para cadad um dos parametros
    */
    public final void readFile() {
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                index = getMapFromLargerFile("Barrel1Index.ser", "Barrel2Index.ser", "Barrel3Index.ser");
            } else {
                urlConnections = getMapFromLargerFile("Barrel1urlConnections.ser", "Barrel2urlConnections.ser", "Barrel3urlConnections.ser");
            }
        }
    }

    // Add a URL to the index for a given word
    public synchronized void add(String word, String url) {
        Set<String> urls = index.get(word);
        if (urls == null) {
            urls = new HashSet<>();
            index.put(word, urls);
        }
        urls.add(url);
    }
    
    // Remove a URL from the index for a given word
    public synchronized void remove(String word, String url) {
        Set<String> urls = index.get(word);
        if (urls != null) {
            urls.remove(url);
            if (urls.isEmpty()) {
                index.remove(word);
            }
        }
    }
    
    // Retrieve the URLs associated with a given word
    public synchronized Set<String> get(String word){
        return index.getOrDefault(word, new HashSet<>());
    } 
    
     // Add a URL to the index for a given word
    public synchronized void addConnections(String word, String url) {
        Set<String> urls = urlConnections.get(word);
        if (urls == null) {
            urls = new HashSet<>();
            urlConnections.put(word, urls);
        }
        urls.add(url);
    }
    
    // Remove a URL from the index for a given word
    public synchronized void removeConnections(String word, String url) {
        Set<String> urls = urlConnections.get(word);
        if (urls != null) {
            urls.remove(url);
            if (urls.isEmpty()) {
                urlConnections.remove(word);
            }
        }
    }
    
    // Retrieve the URLs associated with a given word
    public synchronized Set<String> getConnections(String word){
        return urlConnections.getOrDefault(word, new HashSet<>());
    }
    

    @Override
    public synchronized List<UrlInfo> search(String searchPhrase) {
        String[] keywords = searchPhrase.split(" ");
        Set<String> base = new HashSet<>();
        for (int i = 0; i < keywords.length; i++) {
            if (i != 0){
                Set<String> urls = get(keywords[i]);
                base.retainAll(urls);
            } else {
                base = get(keywords[0]);
            }
        }
        

        List<UrlInfo> results = new ArrayList<>();
        for (String url : base) {
            try {
                Document doc = Jsoup.connect(url).get();
                String title = doc.title();
                String quote = doc.body().text().substring(0, 200);
                int nConnections = (doc.select("a[href]")).size();
                Set<String> pointConnections = getConnections(url);

                UrlInfo newUrl = new UrlInfo(url, title, quote, nConnections, pointConnections);
                
                results.add(newUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!results.isEmpty()){
            List<UrlInfo> sortedResults = new ArrayList<>();
            sortedResults.add(results.get(0));
            for (int i = 1; i < results.size(); i++) {
                UrlInfo currentUrl = results.get(i);
                int j = 0;
                while (j < sortedResults.size() && currentUrl.getnConnections() <= sortedResults.get(j).getnConnections()) {
                    j++;
                }
                sortedResults.add(j, currentUrl);
            }
            return sortedResults;
        }
        return results;
    }
    
    @Override
    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            
            LocalTime currentTime = LocalTime.now();
            LocalTime saveTime = currentTime.plusMinutes(MinutesBetweenSaves);
            
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String[] message = (new String(packet.getData(), 0, packet.getLength())).split(" ");
                //System.out.println("Port: "+ PORT +"   "+message[0]+" "+message[1]+" "+message[2]);
                
                if(message[0].equals("word")){
                    add(message[2],message[1]);
                }else if(message[0].equals("link")){
                    addConnections(message[2],message[1]);
                }
                
                currentTime = LocalTime.now();
                
                if(currentTime.compareTo(saveTime) >= 0){
                    save();
                    saveTime = currentTime.plusMinutes(MinutesBetweenSaves);
                }
                
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
    
    public void save(){
        String filename = "";
        for(int i = 0; i < 2;i++){
            filename = switch (i) {
                case 0 -> name+"Index"+".ser";
                default -> name+"urlConnections"+".ser";
            };
        
        try{
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
                switch (i) {
                    case 0 -> out.writeObject(index);
                    default -> out.writeObject(urlConnections);
                }
            out.close();
            fileOut.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        }
    }
    
    public static void main(String[] args) {
        try {
            Storage_Barrel server1 = new Storage_Barrel("Barrel1",4321);
            server1.start();
            Registry r1 = LocateRegistry.createRegistry(7001);
            r1.rebind("Storage_Barrel_1", server1);
            
            Storage_Barrel server2 = new Storage_Barrel("Barrel2",4321);
            server2.start();
            Registry r2 = LocateRegistry.createRegistry(7002);
            r2.rebind("Storage_Barrel_2", server2);

            
            Storage_Barrel server3 = new Storage_Barrel("Barrel3",4321);
            server3.start();
            Registry r3 = LocateRegistry.createRegistry(7003);
            r3.rebind("Storage_Barrel_3", server3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
}