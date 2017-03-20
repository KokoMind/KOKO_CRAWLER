package com.koko.crawler;

import com.koko.crawler.Crawl;

import java.io.IOException;

public class Main
{

    public static void main(String[] args)
    {
        // write your code here
//        DB mydb = new DB();
//        int ret = mydb.cache_url("www.google.com","192.168.1.1", "5lty", 1);
//        if (ret == 0){
//            System.out.print("Success");
//        }else {
//            System.out.print("Fail");
//        }
//        PriorityQueue<ObjPQueue> pq = new PriorityQueue<>(100);
//        pq.add(new ObjPQueue("x","xx",1000));
//        pq.add(new ObjPQueue("x","xx",1));
//        pq.add(new ObjPQueue("x","xx",-2));
//
//        System.out.println(pq.poll().value);
//        System.out.println(pq.poll().value);
//        System.out.println(Fetcher.internetOn());
        try
        {
            Crawl crawler = new Crawl(args);
            crawler.run();
        }
        catch (IOException e)
        {
            System.err.println("Program is terminated due to invalid arguments");
        }

    }
}
