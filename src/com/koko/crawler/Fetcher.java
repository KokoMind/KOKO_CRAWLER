package com.koko.crawler;

import org.jsoup.Jsoup;

import java.net.URL;
import java.net.URLConnection;


/**
 * Created by mg on 3/20/17.
 */
public class Fetcher {
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

    
}
