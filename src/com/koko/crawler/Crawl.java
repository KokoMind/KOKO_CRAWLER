package com.koko.crawler;

import java.io.IOException;

public class Crawl {

    public void run() {

    }

    public Crawl(String[] args) throws IOException {
        if (check_args(args) == -1)
            throw new IOException();
        System.out.println("Arguments are parsed successfully and we are Ready to crawl");
    }

    public int check_args(String[] args) {
        String firstArg;
        int secondArg;
        if (args.length == 2) {
            try {
                boolean validFirstArg = false;
                secondArg = Integer.parseInt(args[1]);
                for (String s : moods) {
                    if (s.equals(args[0]))
                        validFirstArg = true;
                }
                if (!validFirstArg)
                    return -1;
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[1] + " must be an integer.");
                return -1;
            }
        } else {
            return -1;
        }
        return 0;
    }

    public String moods[] = {
            "init",
            "cont",
    };

    public String seeds[] = {
            "https://en.wikipedia.org",
            "http://www.W3.org",
            "http://web.mit.edu",
            "http://stanford.edu",
            "https://www.rottentomatoes.com",
            "http://www.imdb.com",
            "http://screenrant.com",
            "https://vimeo.com",
            "http://www.100bestwebsites.org",
            "http://www.makeuseof.com/tag/best-websites-internet",
            "https://moz.com/top500",
            "https://www.bloomberg.com",
            "https://www.reddit.com/r/Futurology/comments/48b5oc/best_of_2015_winners",
            "https://moz.com/blog",
            "http://www.berkeley.edu",
            "https://www.cam.ac.uk",
            "http://www.ox.ac.uk",
            "http://www.caltech.edu",
            "http://www.dmoz.org",
            "http://www.ebay.com",
            "https://www.cnet.com",
            "https://www.spotify.com",
            "https://archive.org",
            "http://www.ieee.org",
            "http://www.nike.com",
            "https://en-maktoob.yahoo.com",
    };
}
