/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sd_project;

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
import java.rmi.*;
import java.net.*;
import java.util.*;
import java.rmi.ConnectException;
/**
 *
 * @author Miguel_Fazenda
 */
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
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);) {

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