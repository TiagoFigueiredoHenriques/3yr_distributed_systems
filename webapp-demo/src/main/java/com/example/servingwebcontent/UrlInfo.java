package com.example.servingwebcontent;

import java.io.*;
import java.util.Set;

public class UrlInfo implements Serializable {
    private String link;
    private String title;
    private String quote;
    private int nConnections;
    private Set<String> pointConnections;

    public  UrlInfo(String link, String title, String quote, int nConnections,Set<String> pointConnections) {
        this.link = link;
        this.title = title;
        this.quote = quote;
        this.nConnections = nConnections;
        this.pointConnections = pointConnections;
    }

    public UrlInfo(){}
    
    public void setPointConnections(Set<String> pc){
        pointConnections = pc;
    }
    
    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getQuote() {
        return quote;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getnConnections() {
        return nConnections;
    }
    public void printUrlInfo() {
        System.out.println("Url: " + link + "\nTitle: " + title + "\nQuote: " + quote + "\n");
    }
    public  void printPointConnections() {
        System.out.println("Connections to the Url: \n" + pointConnections + "\n\n");
    }

    public String sPointConnections(){
        String f = "";
        for(String s:pointConnections) {
            f += s + " ";
        }
        return f;
    }
}

