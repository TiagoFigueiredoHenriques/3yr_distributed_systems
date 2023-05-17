package com.example.servingwebcontent;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    /**
    @MessageMapping("/admin")
    @SendTo("/status/servers")
    public Servers servers() throws Exception {
        Servers s = new Servers();

        s.setDownloader(false);
        s.setQueue(false);
        s.setSB1(false);
        s.setSB2(false);
        s.setSB3(false);

        List<Boolean> status;

        do{
            try {

                RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

                status = ri.admin(s.isDownloader(),s.isQueue(),s.isSB1(),s.isSB2(),s.isSB3());

                s.setDownloader(status.get(0));
                s.setQueue(status.get(1));
                s.setSB1(status.get(2));
                s.setSB2(status.get(3));
                s.setSB3(status.get(4));

                return s;

            } catch (Exception e) {
                System.out.println("Exception in main: " + e);
                e.printStackTrace();
            }
        }while(true);

    }

    **/

    @GetMapping("/")
    public String redirect() {
        return "redirect:/Home";
    }

	@GetMapping("/Home")
	public String Home() {
		return "Home";
	}

    @GetMapping("/login")
	public String Login(Model model) {
        model.addAttribute("User",new User());
		return "Login";
	}

    @PostMapping("/login")
    public String UserSubmitingLogin(@ModelAttribute User user,Model model){
        model.addAttribute("User",user);
        try {

            RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

            String output = ri.login(user.getUsername(),user.getPassword());

            if (output.equals("Welcome to the App!")){
                return "App";
            }

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

        return "Login";
    }

    @GetMapping("/register")
    public String Register(Model model) {
        model.addAttribute("User",new User());
        return "Register";
    }

    @PostMapping("/register")
    public String UserSubmitingRegister(@ModelAttribute User user,Model model){
        model.addAttribute("User",user);
        try {
            RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

            Boolean accepted = ri.register(user.getUsername(),user.getPassword());

            if(accepted){
                return "App";
            }else{
                return "Register";
            }

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }
        return "Register";
    }

    @GetMapping("/addurl")
    public String AddUrl(Model model) {
        model.addAttribute("Url",new Url());
        return "AddUrl";
    }

    @PostMapping("/addurl")
    public String UserSubmitingNewUrl(@ModelAttribute Url url,Model model){
        model.addAttribute("Url", url);
        try {
            RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

            ri.send_url(url.getUrl());
        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

        return "App";
    }

    @GetMapping("/searchkeywords")
    public String SearchKeywords(Model model) {
        model.addAttribute("Keywords",new Keywords());
        return "SearchKeywords";
    }

    @PostMapping("/searchkeywords")
    public String UserSubmitingKeywords(@ModelAttribute Keywords keywords,Model model){
        model.addAttribute("Keywords", keywords);

        try {

            RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

            List<UrlInfo> urls = ri.search(keywords.getKeywords());
            Collections.addAll(urls);
            model.addAttribute("arrayUrls", urls);

            return "SearchResults";

        } catch (Exception e) {
            System.out.println("Exception in main: " + e);
            e.printStackTrace();
        }

        return "SearchResults";
    }

    @GetMapping("/searchresults")
    public String SearchResultsMyGod(Model model, @RequestParam(name = "startIndex", defaultValue = "0") Integer startIndex, @RequestParam(name = "arrayUrls")  String [] arrayUrls) {
        model.addAttribute("arrayUrls", arrayUrls);
        model.addAttribute("startIndex", startIndex);

        return "SearchResults";
    }

    @GetMapping("/resultsconnections")
    public String ResultsConnections() {
        return "ResultsConnections";
    }

    @GetMapping("/hackernews")
    public String HackerNews(Model model) {
        model.addAttribute("HackerKeywords",new HackerKeywords());
        return "HackerNews";
    }

    @PostMapping("/hackernews")
    public String UserSubmitingHackerNews(@ModelAttribute HackerKeywords keywords,Model model){
        model.addAttribute("HackerKeywords", keywords);
        try {
            URL url = new URL("https://hacker-news.firebaseio.com/v0/topstories.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // Parse the JSON response containing the top story IDs
            String[] storyIds = response.toString().replace("[", "").replace("]", "").split(",");

            // Index the URLs of the top stories
            for (String storyId : storyIds) {
                indexStoryUrl(storyId.trim(),keywords.getHackerKeywords());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "App";
    }

    @GetMapping("/hackernewsusers")
    public String HackerNewsUsers(Model model) {
        model.addAttribute("HackerUsers",new HackerUsers());
        return "HackerNewsUsers";
    }

    @PostMapping("/hackernewsusers")
    public String UserSubmitingHackerUser(@ModelAttribute HackerUsers hackerUsers,Model model){
        model.addAttribute("HackerUsers", hackerUsers);
        try {
            URL url = new URL("https://hacker-news.firebaseio.com/v0/user/"+ hackerUsers.getHackerUsers() + ".json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // Parse the JSON response containing the top story IDs
            String[] storyIds = response.toString().replace("[", "").replace("]", "").split(",");

            // Index the URLs of the top stories
            for (String storyId : storyIds) {
                indexStoryUrl(storyId.trim(), "-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "App";
    }

    private static void indexStoryUrl(String storyId,String keyword) {
        try {
            URL url = new URL("https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            connection.disconnect();

            // Parse the JSON response containing the story details
            // Extract and index the URL
            // ...
            // Parse the JSON response as HTML using Jsoup
            Document doc = Jsoup.parse(response.toString());

            String[] keywords = keyword.split(" ");

            boolean storyHasKeys = false;
            if(keywords[0].equals("-1")){
                storyHasKeys = true;
            }else {
                for (String key : keywords) {
                    if (doc.body().text().indexOf(key) != -1) {
                        storyHasKeys = true;
                    } else {
                        storyHasKeys = false;
                        break;
                    }
                }
            }
            if(storyHasKeys) {

                // Extract the URL using CSS selector
                Elements urlElements = doc.select("a[href]");

                try {
                    RemoteInterface ri = (RemoteInterface) LocateRegistry.getRegistry(7000).lookup("RMI_Server");

                    for(Element elem: urlElements) {
                        try {
                            String linkUrl = elem.attr("abs:href");
                            if (linkUrl.length() > 0){
                                ri.send_url(linkUrl);
                                System.out.println(linkUrl);
                            }
                        }catch (IOException ex) {
                            Logger.getLogger(NewThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Exception in main: " + e);
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}