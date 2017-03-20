package com.koko.crawler;

import com.koko.crawler.obj.ObjDownloaded;
import org.jsoup.Jsoup;

import java.net.URL;
import java.net.URLConnection;


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
            return true;
    }


}
