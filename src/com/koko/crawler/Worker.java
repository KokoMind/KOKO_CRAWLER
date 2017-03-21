package com.koko.crawler;

import com.koko.crawler.obj.ObjDownloaded;
import com.koko.crawler.obj.ObjExtractedLink;
import com.koko.crawler.obj.ObjPQueue;

public class Worker extends Thread
{

    private Thread t;
    private int id;
    private String name;
    private Frontier frontier;
    private DB db;
    private Dashboard dash;
    private int crawled = 0;
    private int refused = 0;
    private volatile boolean keepOn = true;

    public Worker(int thread_id, String thread_name, Frontier front, DB db_, Dashboard dash_)
    {
        id = thread_id;
        name = thread_name;
        frontier = front;
        db = db_;
        dash = dash_;
        this.setDaemon(true);
    }

    public void run()
    {
        Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());
        try
        {
            while (true)
            {
                System.out.println("Next_Url___");
                ObjPQueue obj_pq = frontier.get_url(id);
                if (obj_pq == null)
                {
                    System.out.println("Empty_Queue");
                    continue;
                }
                System.out.println("Downloading");
                ObjDownloaded obj_d = null;//Fetcher.fetch(obj_pq.url);
                if (obj_d == null)
                {
                    this.refused++;
                    System.out.println("Refused_Url");
                    continue;
                }
                this.crawled++;
                System.out.println("Valid_Url__");
                ObjExtractedLink extracted_links[] = ObjExtractedLink.setup_extracted_links(obj_d.links, obj_d.content.length(), obj_pq.value);
                frontier.push_to_serve(extracted_links, id);
                int ret = db.cache_url(obj_pq.url, obj_pq.dns, obj_d.content, id);
                if (ret == -1)
                {
                    System.err.println("Cannot cache the link in the database");
                }
            }
        }
        finally
        {
            System.out.println("Thread " + id + " exiting.");
        }

    }

    public void start()
    {
        System.out.println("Starting Thread " + id);
        if (t == null)
        {
            t = new Thread(this, name);
            t.start();
        }
    }

    public class RunWhenShuttingDown extends Thread
    {
        public void run()
        {
//            System.out.println("Control-C caught. Shutting down...");
            keepOn = false;
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
