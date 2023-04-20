/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package sd_project;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.*;

/**
 *
 * @author Miguel_Fazenda
 */
public interface RemoteInterfaceSB extends Remote {
     public List<UrlInfo>  search(String searchPhrase)throws RemoteException;
}
