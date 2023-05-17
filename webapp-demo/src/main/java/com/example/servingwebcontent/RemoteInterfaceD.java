package com.example.servingwebcontent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterfaceD extends Remote {
    public boolean alive()throws RemoteException;
}