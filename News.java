package com.company;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class News {
    public WebDriver WDname;
    public static ArrayList<HashMap<String,String>> newsList = new ArrayList<>();
    public static ArrayList<String> links = new ArrayList<>();

    public static final String BASE_URL_BBC = "https://www.bbc.com/news/world/africa";
    public static final String BASE_URL_CNBC = "https://www.cnbcafrica.com/category/news/";

    public static final String MY_WEBSITE_URL = "http://localhost:8000/news/add";
    public static final int INTERVAL = 10000; //300000

    public static void main(String[] args){
        System.setProperty("webdriver.chrome.driver","chromedriver.exe");


        while(true){
            newsList = new ArrayList<>();
            links.clear();

            WebDriver WDname = new ChromeDriver();
            WDname.navigate().to(BASE_URL_BBC);

            // get links from bbc news
            List<WebElement> topNews1 = WDname.findElements(By.cssSelector("#comp-candy-asset-munger-2 .title-link"));
            List<WebElement> topNews2 = WDname.findElements(By.cssSelector("#comp-candy-asset-munger-3 .title-link"));
            topNews1.addAll(topNews2);

            
            for (WebElement link:topNews1.subList(0,6)){
                String lin= link.getAttribute("href");

                if(lin.contains("www.bbc.com/news/world-africa")){
                    links.add(lin);
                }
            }
            newsList.addAll(getNews(WDname, links, ".story-body__h1", ".story-body__inner p"));

            // reset links list
            links.clear();


            WDname.navigate().to(BASE_URL_CNBC);

            // get links from cnbc news
            List<WebElement> topNews3 = WDname.findElements(By.cssSelector(".td_module_10 .entry-title a")).subList(0,6);

            for (WebElement link:topNews3){
                String lin= link.getAttribute("href");

                links.add(lin);
            }

            newsList.addAll(getNews(WDname, links, ".post .td-post-header .entry-title",".td-post-content p"));

            if(newsList.size()>0){
                addNews(WDname, newsList);
            }

            WDname.close();

            try{
                Thread.sleep(INTERVAL);
            }catch (Exception e){
                System.out.println("Thread sleep error");
            }
        }
    }

    public static ArrayList<HashMap<String, String>> getNews(WebDriver WDname, List<String> links, String titleSelector, String contentSelector){
        ArrayList<HashMap<String,String>> newsList = new ArrayList<>();


        for(int i=0;i<links.size();i++){
            HashMap<String, String> news = new HashMap<>();

            String link = links.get(i);

            WDname.navigate().to(link);

            WebElement title = WDname.findElement(By.cssSelector(titleSelector));
            news.put("title", title.getText());

            List<WebElement> contents = WDname.findElements(By.cssSelector(contentSelector));

            String contentStr = "";

            for (WebElement content:contents){
                String par = content.getText();
                par = par.replaceAll("[^\\x00-\\x7F]","");
                contentStr += par + "\n\n";
            }

            news.put("content", contentStr);
            System.out.println(contentStr);

            newsList.add(news);
        }

        return newsList;
    }

    public static void addNews(WebDriver WDname, ArrayList<HashMap<String, String>> newsList){
        WDname.navigate().to(MY_WEBSITE_URL);

        for(HashMap<String, String> n:newsList){
            WebElement title = WDname.findElement(By.name("title"));
            WebElement content = WDname.findElement(By.name("content"));

            title.clear();
            title.sendKeys(n.get("title"));

            content.clear();
            content.sendKeys(n.get("content"));

            content.submit();
        }

    }

}
