package com.example.servingwebcontent;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteInterface extends Remote{
    public void send_url(String url)throws RemoteException;
    public List<UrlInfo> search(String searchPhrase)throws RemoteException;
    public boolean register(String username, String password)throws RemoteException;
    public String login(String username, String password)throws RemoteException;

    public List<Boolean> admin(Boolean pastD,Boolean pastQ,Boolean pastSB1,Boolean pastSB2,Boolean pastSB3)throws RemoteException;
}
