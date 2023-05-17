package com.example.servingwebcontent;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.*;

public interface RemoteInterfaceSB extends Remote {
     public List<UrlInfo>  search(String searchPhrase)throws RemoteException;

     public boolean alive()throws RemoteException;
}
