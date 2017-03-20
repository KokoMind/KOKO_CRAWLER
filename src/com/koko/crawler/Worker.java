package com.koko.crawler;

import com.koko.crawler.obj.ObjPQueue;

public class Worker extends Thread
{

    private Thread t;
    private int id;
    private String name;
    private Frontier frontier;
    private Dashboard dash;
    private int crawled = 0;
    private int refused = 0;

    public Worker(int thread_id, String thread_name, Frontier front, Dashboard dash_)
    {
        id = thread_id;
        name = thread_name;
        frontier = front;
        dash = dash_;
        this.setDaemon(true);
    }

    public void run()
    {

        try
        {
            while (true)
            {
                System.out.println("Next_Url___");
                ObjPQueue obj = frontier.get_url(id);
                if (obj == null)
                {
                    System.out.println("Empty_Queue");
                    continue;
                }
                System.out.println("Downloading");
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

}
