package com.koko.crawler;

import com.koko.crawler.obj.ObjDownloaded;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.koko.crawler.obj.ObjLink;
import com.panforge.robotstxt.RobotsTxt;
import com.shekhargulati.urlcleaner.UrlCleaner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Fetcher {
    /* Class that will fetch the page, validate that it is of type HTML, extract its contents and hyperlinks */


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
        String sep = "\\", altsep = "/", extsep = ".";
        int sepIndex = url.lastIndexOf(sep);
        int altsepIndex = url.lastIndexOf(altsep);
        sepIndex = Math.max(sepIndex, altsepIndex);

        int dotIndex = url.lastIndexOf(extsep);
        if (dotIndex > sepIndex) {   //skip all leading dots
            int filenameIndex = sepIndex + 1;
            while (filenameIndex < dotIndex) {
                if (!url.substring(filenameIndex, filenameIndex + 1).equals(extsep))
                    return url[:dotIndex],p[dotIndex:];
                filenameIndex += 1;
            }
            return false;
        }

    public static Document downloadPage(String url) {
        if (Fetcher.checkURL(url) && Fetcher.checkRobots(url)) {
            try {
                return Jsoup.connect(url).get();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static String extractASCIIOnly(String data) throws UnsupportedEncodingException {
        data = new String(Charset.forName("ascii").encode(data).array(), "ascii");
        data = data.replace('?', ' ');
        return data;
    }

    public static ObjDownloaded fetch(String url) {
        Document soup = Fetcher.downloadPage(url);
        return null;
    }

    public static String testfetch(String url) {
        try {
            Document soup = Fetcher.downloadPage(url);
            String content = extractContent(soup);
            ObjLink[] links = null;
            links = extractLinks(soup).toArray(links);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static ArrayList<ObjLink> extractLinks(Document soup) {
        Elements links = soup.select("a");
        ArrayList<ObjLink> stringLinks = new ArrayList<>();
        for (Element link : links) {
            String absHref = link.attr("abs:href"); // "http://jsoup.org/
            String normalizedURL = UrlCleaner.normalizeUrl(absHref);
            if (Fetcher.checkExtHTML(normalizedURL))
                stringLinks.add(new ObjLink(normalizedURL, Fetcher.extractDNS(normalizedURL)));
        }
        return stringLinks;
    }


    public static String extractContent(Document soup) throws UnsupportedEncodingException {
        return extractASCIIOnly(soup.text()).trim().replaceAll(" +", " ");
    }

    public static void main(String[] args) {
        testfetch("https://en.wikipedia.org/wiki/URL_normalization");
    }


}
