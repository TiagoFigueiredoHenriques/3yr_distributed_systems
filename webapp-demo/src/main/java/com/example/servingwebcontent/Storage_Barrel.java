package com.example.servingwebcontent;

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
import java.util.logging.Level;
import java.util.logging.Logger;


/*
A classe Storage_Barrel guarda e destriubui as informacoes conseguidas pelos downloaders, estes transmitem essa informacao atraves do processo multicast.
A classe tambem esta ligada por RMI atraves da RemoteInterfaceSB, permitindo a esta utilizar alguns dos seus metedos
*/

public class Storage_Barrel extends Thread implements RemoteInterfaceSB,Serializable{
    private String MULTICAST_ADDRESS1 = "224.3.2.1";
    private String MULTICAST_ADDRESS2 = "224.3.2.2";
    private String MULTICAST_ADDRESS3 = "224.3.2.3";
    private int PORT1, PORT2, PORT3;
    private String name;
    private final int timeBetweenSaves = 60000;

    private HashMap<String, Set<String>> index;


    private HashMap<String, Set<String>> urlConnections;

    private HashMap<String, UrlInfo> urlInfoMap; // need to take out when we read file

    public Storage_Barrel(String name,int port_m1,int port_m2,int port_m3) {
        super("Server " + (long) (Math.random() * 1000));
        PORT1 = port_m1;
        PORT2 = port_m2;
        PORT3 = port_m3;
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

    public final HashMap<String, UrlInfo> getMapFromLargerFileUrlInfo(String filename1, String filename2, String filename3) {
        FileInputStream fileIn1 = null;
        FileInputStream fileIn2 = null;
        FileInputStream fileIn3 = null;

        HashMap<String, UrlInfo> result = null;

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
                    result = (HashMap<String, UrlInfo>) in.readObject();
                    in.close();
                } else {
                    ObjectInputStream in = new ObjectInputStream(fileIn3);
                    result = (HashMap<String, UrlInfo>) in.readObject();
                    in.close();
                }
            } else {
                if (fileSize2 > fileSize3) {
                    ObjectInputStream in = new ObjectInputStream(fileIn2);
                    result = (HashMap<String, UrlInfo>) in.readObject();
                    in.close();
                } else {
                    ObjectInputStream in = new ObjectInputStream(fileIn3);
                    result = (HashMap<String, UrlInfo>) in.readObject();
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
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                index = getMapFromLargerFile("Barrel1Index.ser", "Barrel2Index.ser", "Barrel3Index.ser");
            } else if (i == 2) {
                urlConnections = getMapFromLargerFile("Barrel1urlConnections.ser", "Barrel2urlConnections.ser", "Barrel3urlConnections.ser");
            }else{
                urlInfoMap = getMapFromLargerFileUrlInfo("Barrel1UrlInfo.ser", "Barrel2UrlInfo.ser", "Barrel3UrlInfo.ser");
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


    public synchronized Set<String> getConnections(String word){
        return urlConnections.getOrDefault(word, new HashSet<>());
    }

    public synchronized void addUrlInfo(String word, UrlInfo urlInfo) {
        urlInfoMap.put(word, urlInfo);
    }

    public synchronized UrlInfo getUrlInfo(String word){
        return urlInfoMap.get(word);
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
                UrlInfo newUrl = getUrlInfo(url);
                if(newUrl == null){
                    Document doc = Jsoup.connect(url).get();
                    String title = doc.title();
                    String quote;
                    if (doc.body().text().length() < 200) {
                        quote = doc.body().text(); // Use the entire body text if it's less than 200 characters
                    } else {
                        quote = doc.body().text().substring(0, 200); // Take the first 200 characters if it's more than or equal to 200 characters
                    }
                    int nConnections = (doc.select("a[href]")).size();
                    newUrl = new UrlInfo(url, title, quote, nConnections, null);
                }
                Set<String> pointConnections = getConnections(url);

                newUrl.setPointConnections(pointConnections);

                results.add(newUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!results.isEmpty()){
            List<UrlInfo> sortedResults = new ArrayList<>(results);
            sortedResults.sort(Comparator.comparingInt(UrlInfo::getnConnections).reversed());
            return sortedResults;
        }

        return results;
    }

    @Override
    public void run() {
        try {
            Thread messageThread = new Thread(()->{
                MulticastSocket socket1 = null;
                try {
                    socket1 = new MulticastSocket(PORT1);  // create socket and bind it
                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS1);
                    socket1.joinGroup(group);

                    while (true) {
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket1.receive(packet);

                        String[] message = (new String(packet.getData(), 0, packet.getLength())).split(" ");
                        //System.out.println("Port: "+ PORT1 +"   "+message[0]+" "+message[1]+" "+message[2]);


                        if(message[0].equals("word")){
                            add(message[2],message[1]);
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socket1.close();
                }
            });


            Thread urlInfoThread = new Thread(()->{
                MulticastSocket socket2 = null;
                try {
                    socket2 = new MulticastSocket(PORT2);  // create socket and bind it
                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS2);
                    socket2.joinGroup(group);

                    while (true) {
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket2.receive(packet);

                        ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
                        ObjectInputStream ois = new ObjectInputStream(bis);
                        try {
                            UrlInfo urlInfo = (UrlInfo) ois.readObject();

                            addUrlInfo(urlInfo.getLink(), urlInfo);

                            //System.out.println("Port: "+ PORT2 +"  "+ urlInfo.getLink());
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Storage_Barrel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        bis.close();
                        ois.close();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socket2.close();
                }
            });

            Thread linkThread = new Thread(()->{
                MulticastSocket socket3 = null;
                try {
                    socket3 = new MulticastSocket(PORT3);  // create socket and bind it
                    InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS3);
                    socket3.joinGroup(group);

                    while (true) {
                        byte[] buffer = new byte[2048];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket3.receive(packet);

                        String[] message = (new String(packet.getData(), 0, packet.getLength())).split(" ");
                        //System.out.println("Port: "+ PORT3 +"   "+message[0]+" "+message[1]+" "+message[2]);

                        if(message[0].equals("link")){
                            addConnections(message[2],message[1]);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socket3.close();
                }
            });

            Thread saveThread = new Thread(()->{
                while(true){
                    try {
                        Thread.sleep(timeBetweenSaves);
                        save();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Storage_Barrel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            });

            messageThread.start();
            urlInfoThread.start();
            linkThread.start();
            saveThread.start();

            messageThread.join();
            urlInfoThread.join();
            linkThread.join();
            saveThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Storage_Barrel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void save(){
        String filename = "";
        for(int i = 0; i < 3;i++){
            filename = switch (i) {
                case 0 -> name+"Index.ser";
                case 1 -> name+"UrlInfo.ser";
                default -> name+"urlConnections.ser";
            };

            try{
                FileOutputStream fileOut = new FileOutputStream(filename);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
            /*
            if(i == 0){
                    synchronized(index){
                        out.writeObject(index);
                    }
                }else if(i == 1){
                    synchronized(urlInfoMap){
                        out.writeObject(urlInfoMap);
                    }
                }else{
                    synchronized(urlConnections){
                        out.writeObject(urlConnections);
                    }
                }
                */
                switch (i) {
                    case 0 -> out.writeObject(index);
                    case 1 -> out.writeObject(urlInfoMap);
                    default -> out.writeObject(urlConnections);
                }


                out.close();
                fileOut.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean alive(){
        return true;
    }

    public static void main(String[] args) {
        try {
            Storage_Barrel server1 = new Storage_Barrel("Barrel1",4321,4322,4323);
            server1.start();
            Registry r1 = LocateRegistry.createRegistry(7001);
            r1.rebind("Storage_Barrel_1", server1);

            Storage_Barrel server2 = new Storage_Barrel("Barrel2",4321,4322,4323);
            server2.start();
            Registry r2 = LocateRegistry.createRegistry(7002);
            r2.rebind("Storage_Barrel_2", server2);


            Storage_Barrel server3 = new Storage_Barrel("Barrel3",4321,4322,4323);
            server3.start();
            Registry r3 = LocateRegistry.createRegistry(7003);
            r3.rebind("Storage_Barrel_3", server3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}