package com.example.servingwebcontent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.*;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.rmi.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMI_Search_Module extends UnicastRemoteObject implements RemoteInterface,Serializable{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 1236;

    private static final long serialVersionUID = 1L;
    
    private HashMap<String, String> usersInfo;
    
    private String[] barrels = {"Storage_Barrel_1","Storage_Barrel_2","Storage_Barrel_3"};
    private int nBarrel = 0;

    public RMI_Search_Module() throws RemoteException {
        super();
        readFile();
    }
    
    public final void readFile(){
        try {
            FileInputStream fileIn = new FileInputStream("usersInfo.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            usersInfo = (HashMap<String, String>) in.readObject();
            fileIn.close();
            in.close();
        }catch (FileNotFoundException e) {
            usersInfo = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void save(){
        try{
            synchronized(usersInfo){
                FileOutputStream fileOut = new FileOutputStream("usersInfo.ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(usersInfo);
                out.close();
                fileOut.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean register(String username, String password) {
        if (usersInfo.containsKey(username)) {
            return false;
        } else {
            usersInfo.put(username, password);
            save();
            return true;
        }
        
    }
    
    @Override
    public String login(String username, String password) {
        if (!usersInfo.containsKey(username)) {
            return "Error.: username not found!";
        }

        if (!usersInfo.get(username).equals(password)){
            return "Error.: invalid password!";
        }

        return "Welcome to the App!";
    }
    
    @Override
    public void send_url(String url){
        System.out.println("Hello2");

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {
            System.out.println("Hello3");

            out.println("SEND 0");
            out.println(url);   
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
    
    @Override
    public List<UrlInfo> search(String searchPhrase){
        List<UrlInfo>  urls = null;
        
        int end = nBarrel;
        do{
        try {
            
            RemoteInterfaceSB ri = (RemoteInterfaceSB) LocateRegistry.getRegistry(nBarrel+7001).lookup(barrels[nBarrel]);
            if(nBarrel == 2){
                nBarrel = 0;
            }else{
                nBarrel++;
            }
            
            urls = ri.search(searchPhrase);
           
            return urls;
            
        }catch(ConnectException ex){
            if(nBarrel == 2){
                nBarrel = 0;
            }else{
                nBarrel++;
            }
            System.out.println("ConnectException");
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }
        }while(nBarrel!=end);
        return urls;
    }

    @Override
    public List<Boolean> admin(Boolean pastD,Boolean pastQ,Boolean pastSB1,Boolean pastSB2,Boolean pastSB3) {

        boolean presentD = false, presentQ = false, presentSB1 = false, presentSB2 = false, presentSB3 = false;

        List<Boolean> resultsO = new ArrayList<>();

        resultsO.add(presentD);
        resultsO.add(presentQ);
        resultsO.add(presentSB1);
        resultsO.add(presentSB2);
        resultsO.add(presentSB3);


        while (true) {
            try {

                RemoteInterfaceD riD = (RemoteInterfaceD) LocateRegistry.getRegistry(7010).lookup("Downloader");

                presentD = riD.alive();
            } catch (ConnectException ex) {
                presentD = false;
                System.out.println("ConnectException D");
            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
            }
            try {

                RemoteInterfaceQ riQ = (RemoteInterfaceQ) LocateRegistry.getRegistry(7011).lookup("Queue");

                presentQ = riQ.alive();
            } catch (ConnectException ex) {
                presentQ = false;
                System.out.println("ConnectException Q");
            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
            }
            try {

                RemoteInterfaceSB riSB1 = (RemoteInterfaceSB) LocateRegistry.getRegistry(7001).lookup("Storage_Barrel_1");

                presentSB1 = riSB1.alive();
            } catch (ConnectException ex) {
                presentSB1 = false;
                System.out.println("ConnectException SB1");
            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
            }
            try {

                RemoteInterfaceSB riSB2 = (RemoteInterfaceSB) LocateRegistry.getRegistry(7002).lookup("Storage_Barrel_2");

                presentSB2 = riSB2.alive();
            } catch (ConnectException ex) {
                presentSB2 = false;
                System.out.println("ConnectException SB2");
            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
            }
            try {

                RemoteInterfaceSB riSB3 = (RemoteInterfaceSB) LocateRegistry.getRegistry(7003).lookup("Storage_Barrel_3");

                presentSB3 = riSB3.alive();
            } catch (ConnectException ex) {
                presentSB3 = false;
                System.out.println("ConnectException SB3");
            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
            }

            if (pastD != presentD || pastQ != presentQ || pastSB1 != presentSB1 || pastSB2 != presentSB2 || pastSB3 != presentSB3) {

                List<Boolean> results = new ArrayList<>();

                results.add(presentD);
                results.add(presentQ);
                results.add(presentSB1);
                results.add(presentSB2);
                results.add(presentSB3);

                return results;

            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(RMI_Search_Module.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String args[]){ 
        try {
            RMI_Search_Module rmi_s_m = new RMI_Search_Module();
            Registry r = LocateRegistry.createRegistry(7000);
            r.rebind("RMI_Server", rmi_s_m);
            System.out.println("Hello Server ready.");    
        } catch (RemoteException re) {
            System.out.println("Exception in RemoteInterface.main: " + re);
        }

    }
}