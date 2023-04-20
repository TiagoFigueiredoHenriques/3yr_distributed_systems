/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sd_project;

/**
 *
 * @author Miguel_Fazenda
 */
        
import java.rmi.registry.LocateRegistry;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

        
public class RMI_Client {
    
    public static void send_urls(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Write the url: ");
        String urls = sc.nextLine();
        try {

            RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

            ri.send_url(urls);
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }
    }
    
    public static void search(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Write the keywords seperated by a space: ");
        String searchPhrase = sc.nextLine();
        
        try {

            RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

            List<UrlInfo> urls = ri.search(searchPhrase);
            
            if(urls != null){
                System.out.println("Resultados obtidos:" + urls.size()+"\n\n");
            
                int escolha = 1;
                int i = 0;
                int max = 0;
                while(escolha != 0){
                    if ( urls.isEmpty()){
                        System.out.println("No Matches Found");
                        break;
                    }
                    if(i + 10 < urls.size()){
                        max = i + 10;
                        for(;i < max; i++){
                            urls.get(i).printUrlInfo();
                            if(escolha == 2){
                                urls.get(i).printPointConnections();
                            }
                        }
                    }else{
                        for(;i < urls.size(); i++){
                            urls.get(i).printUrlInfo();
                            if(escolha == 2){
                                urls.get(i).printPointConnections();
                            }
                            escolha = 0;
                        }
                    }

                    if(escolha != 0){
                        System.out.println("1 next page; 2 show connections to page; 0 exit");
                        try{
                            escolha = sc.nextInt();
                        }catch(InputMismatchException e){
                            sc.next();
                            escolha = 1;
                        }
                    }

                }
            }else{
                System.out.println("No Matches Found!");
            }
            
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }
    }
   
    public static void app(){
        Scanner sc = new Scanner(System.in);
        int escolha;
        
        do { 
            System.out.println("\n1 -Send Url");
            System.out.println("2 -Search");
            System.out.println("0 -Sair da aplicacao");
            try{
                escolha = sc.nextInt();//Pedir ao utilizador o numero de produtos
            }catch(InputMismatchException e){
                sc.next();
                escolha = -1;
            } 
            switch(escolha) {
                case 1: send_urls(); break;
                case 2: search();break;
                case 0: System.exit(0);
                default: System.out.println("Número inválido");
            }
        } while (escolha != 0);
    }
    
    public static void register(){
        Scanner sc = new Scanner(System.in);
        boolean accepted = false;
        int escolha = 0;
        do{
            System.out.println("Write the username: ");
            String username = sc.nextLine();
            System.out.println("Write the password: ");
            String password = sc.nextLine();
            try {

                RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

                accepted = ri.register(username,password);

            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
            }
            if(!accepted){
                System.out.println("Utilizador inválido \n 1 Try again; 0 Exit");
                try{
                    escolha = sc.nextInt();//Pedir ao utilizador o numero de produtos
                }catch(InputMismatchException e){
                    sc.next();
                    escolha = 0;
                }
            }
            if(escolha == 0){
                accepted = true;    
            }
        }while(!accepted);
    }
    
    public static boolean login(){
        Scanner sc = new Scanner(System.in);
        int escolha = 0;
        do{
            System.out.println("Write the username: ");
            String username = sc.nextLine();
            System.out.println("Write the password: ");
            String password = sc.nextLine();
            try {

                RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

                String output = ri.login(username,password);
                
                System.out.println(output);
                
                if (output.equals("Welcome to the App!")){
                    return true;
                }
                
            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
            }
            if(escolha == 0){
                System.out.println("1 Try again; 0 Exit");
                try{
                    escolha = sc.nextInt();//Pedir ao utilizador o numero de produtos
                }catch(InputMismatchException e){
                    sc.next();
                    escolha = 0;
                }
            }
        }while(escolha != 0);
        
        return false;
        
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int escolha;
        
        do { 
            System.out.println("\n1 -Register");
            System.out.println("2 -Login");
            System.out.println("0 -Sair da aplicacao");
            try{
                escolha = sc.nextInt();//Pedir ao utilizador o numero de produtos
            }catch(InputMismatchException e){
                sc.next();
                escolha = -1;
            } 
            switch(escolha) {
                case 1: register(); break;
                case 2: if(login()){app();};break;
                case 0: System.exit(0);
                default: System.out.println("Número inválido");
            }
        } while (escolha != 0);
    }

}
