package com.koko.crawler;

import com.koko.crawler.obj.ObjDownloaded;

import java.io.File;
import java.io.InputStream;
import java.net.*;

import com.panforge.robotstxt.RobotsTxt;
import org.apache.commons.io.FilenameUtils;

public class Fetcher {
    /* Class that will fetch the page, validate that it is of type HTML, extract its contents and hyperlinks */

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
        /* Check that our crawler satisfies robot exclusion standard */
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

    public static String extractDNS(String url) {
        try {
            InetAddress address = InetAddress.getByName(new URL(url).getHost());
            return address.getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean checkExtHTML(String url) {
        /* check the filename extension either HTML or "" */
        //Implementations in Java are pieces of crap.
        return false;
    }

    //public static void main(String[] args) {
      //  System.out.println(checkExtHTML("http://ilpubs.stanford.edu"));
    //}


}
