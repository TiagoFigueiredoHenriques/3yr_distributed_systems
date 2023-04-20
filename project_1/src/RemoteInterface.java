/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sd_project;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Miguel_Fazenda
 */
public interface RemoteInterface extends Remote{
    public void send_url(String url)throws RemoteException;
    public List<UrlInfo> search(String searchPhrase)throws RemoteException;
    public boolean register(String username, String password)throws RemoteException;
    public String login(String username, String password)throws RemoteException;
}
