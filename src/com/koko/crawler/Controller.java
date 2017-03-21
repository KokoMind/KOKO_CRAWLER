package com.koko.crawler;


import com.koko.crawler.obj.ObjExtractedLink;
import com.koko.crawler.obj.ObjLink;

public class Controller
{

    public int num_workers;
    public Worker workers[];
    public Frontier frontier;
    public DB db;
    public Dashboard dash;
    private volatile boolean keepOn = true;

    public Controller(int num_threads, String[] seeds, int mode)
    {
        num_workers = num_threads;
        workers = new Worker[num_threads];
        dash = new Dashboard();
        db = new DB();
        frontier = new Frontier(num_threads, db, dash);

        for (int i = 0; i < num_workers; i++)
        {
            workers[i] = new Worker(i, "Thread-" + String.valueOf(i), frontier, db, dash);
        }
        System.out.println("Workers Created");

        ObjExtractedLink links[] = setup_seeds(seeds);

        if (mode == 0) // Mode init
        {
            frontier.distribute_seeds(links);
            System.out.println("Seeds distributed");
        }
        else if (mode == 1) //Mode cont
        {
            frontier.load_to_crawl();
        }
    }

    public void run()
    {
        Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());
        try
        {
            for (int i = 0; i < num_workers; i++)
                workers[i].start();

            //TODO Periodic Thread to save to_crawl

            frontier.distribute();

            for (int i = 0; i < num_workers; i++)
                workers[i].join();
            System.out.println("All Workers Exit");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            frontier.save_to_crawl();
            System.out.println("Saved EL7");
        }
    }

    public ObjExtractedLink[] setup_seeds(String[] seeds)
    {
        System.out.println("Setting the Seeds");
        ObjLink links[] = new ObjLink[seeds.length];
        int sz = seeds.length;
        for (int i = 0; i < sz; i++)
        {
            links[i] = new ObjLink(seeds[i], Fetcher.extractDNS(seeds[i]));
        }
        ObjExtractedLink ret[] = new ObjExtractedLink[links.length];
        for (int i = 0; i < sz; i++)
        {
            ret[i] = new ObjExtractedLink(links[i], 100000, 100000, 1);
        }
        System.out.println("Seeds are ready");
        System.out.println("Number of seeds = " + String.valueOf(ret.length));
        return ret;
    }

    public class RunWhenShuttingDown extends Thread
    {
        public void run()
        {
//            System.out.println("Control-C caught. Shutting down...");
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
