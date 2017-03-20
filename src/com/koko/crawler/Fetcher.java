package com.koko.crawler;

import com.koko.crawler.obj.ObjDownloaded;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.panforge.robotstxt.RobotsTxt;

public class Fetcher {

    public static ObjDownloaded fetch(String url) {
        return null;
    }

    public static String downloadPage(String url) {
        return "";
    }

    public static boolean internetOn() {
        for (int i = 0; i < 5; i++) {
            try {
                URL url = new URL("https://www.google.com/");
                URLConnection connection = url.openConnection();
                connection.connect();
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static boolean checkRobots(String url) {
        try {
            if (url.charAt(url.length() - 1) != '/')
                url = url + '/';
            InputStream robotsTxtStream = new URL(url + "robots.txt").openStream();
            RobotsTxt robotsTxt = RobotsTxt.read(robotsTxtStream);
            return robotsTxt.query("*", "/");
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean checkURL(String url) {
        /* Checks that URL is alive and of has a response of type html. */
        while (true) {
            try {
                URL urll = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urll.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    String contentType = connection.getHeaderField("content-type");
                    return contentType.contains("html");
                } else return false;

            } catch (Exception e) {
                if (Fetcher.internetOn())
                    return false;
                System.out.print("\rConnection lost. Retry!!");
            }
        }
    }

    //public static void main(String[] args) {
        //System.out.println(checkURL("http://ilpubs.stanford.edu:8090/733/1/2002-9.pdf"));
    //}


}
