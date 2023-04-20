/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sd_project;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Miguel_Fazenda
 */
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

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getQuote() {
        return quote;
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
}

