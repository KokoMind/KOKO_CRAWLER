package com.koko.crawler;

import com.koko.crawler.Crawl;

import java.io.IOException;

public class Main
{

    public static void main(String[] args)
    {
        try
        {
            Crawl crawler = new Crawl(args);
            crawler.run();
            System.out.println("BYE BYE Atmna akon naf3tak :')");
        }
        catch (IOException e)
        {
            System.err.println("Program is terminated due to invalid arguments");
        }

    }
}
