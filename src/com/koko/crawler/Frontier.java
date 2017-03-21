package com.koko.crawler;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.koko.crawler.interrupt_shut.*;
import com.koko.crawler.obj.ObjExtractedLink;
import com.koko.crawler.obj.ObjLinkProp;
import com.koko.crawler.obj.ObjPQueue;
import com.koko.crawler.obj.ObjThreadProp;

public class Frontier implements IShutdownThreadParent
{

    public int num_threads;
    public Dashboard dash;
    private DB db;

    private int turn = -1;
    private volatile boolean keepOn = true;

    public ObjThreadProp[] threads_prop;
    private BlockingQueue<ObjExtractedLink>[] to_serve;
    private PriorityBlockingQueue<ObjPQueue>[] queues;
    private HashMap<String, Integer>[] attended_websites;

    private ShutdownThread fShutdownThread;

    public Frontier(int num_threads, DB db_, Dashboard dash_)
    {
        this.num_threads = num_threads;
        this.dash = dash_;
        this.db = db_;

        //initialize all Arrays of the threads
        threads_prop = new ObjThreadProp[num_threads];
        to_serve = new BlockingQueue[num_threads];
        queues = new PriorityBlockingQueue[num_threads];
        attended_websites = new HashMap[num_threads];

        for (int i = 0; i < num_threads; i++)
        {
            threads_prop[i] = new ObjThreadProp();
            to_serve[i] = new ArrayBlockingQueue<ObjExtractedLink>(100000);
            queues[i] = new PriorityBlockingQueue<ObjPQueue>(100000);
            attended_websites[i] = new HashMap<String, Integer>();
        }

        //For interrupt
        fShutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(fShutdownThread);
    }

    @Override
    public void shutdown()
    {
        // code to cleanly shutdown your Parent instance.
        System.out.println("Frontier Exit");
    }

    public void push_to_serve(ObjExtractedLink extracted_links[], int thread_id)
    {
        int sz = extracted_links.length;
        for (int i = 0; i < sz; i++)
        {
            to_serve[thread_id].offer(extracted_links[i]);
        }
    }

    public ObjPQueue get_url(int thread_id)
    {
        try
        {
            return queues[thread_id].poll(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private ObjExtractedLink pop_to_distribute()
    {
        while (true)
        {
            for (int i = 0; i < num_threads; i++)
            {
                ObjExtractedLink ret = to_serve[i].poll();
                if (ret != null)
                    return ret;
            }
        }
    }

    public void distribute()
    {
        try
        {
            System.out.println("Frontier Started");
            while (keepOn)
            {
                ObjExtractedLink link = pop_to_distribute();

                System.out.println("Serving a link");

                if (db.hash(link.link.url) == -1 || link.link.dns == null)
                    continue;
                double value = calc_priority(link.prop);

                boolean dns_found = false;

                for (int i = 0; i < num_threads; i++)
                    if (attended_websites[i].containsKey(link.link.dns))
                    {
                        queues[i].offer(new ObjPQueue(link.link.url, link.link.dns, value));
                        threads_prop[i].inc_dns();
                        dns_found = true;
                        break;
                    }

                if (!dns_found)
                {
                    int turn_thread = get_turn();
                    queues[turn_thread].offer(new ObjPQueue(link.link.url, link.link.dns, value));
                    attended_websites[turn_thread].put(link.link.dns, 1);
                    threads_prop[turn_thread].inc_dns();
                    threads_prop[turn_thread].setTo_crawl(queues[turn_thread].size());
                }
            }
        }
        finally
        {
            System.out.println("Frontier Exit");
        }
    }

    public void distribute_seeds(ObjExtractedLink[] links)
    {
        System.out.println("Distributing Seeds");

        for (ObjExtractedLink link : links)
        {
            System.out.println("Serving a seed");

            if (/*db.hash(link.link.url) == -1 || */link.link.dns == null)
                continue;
            double value = calc_priority(link.prop);

            turn++;
            turn %= num_threads;
            queues[turn].add(new ObjPQueue(link.link.url, link.link.dns, value));
            attended_websites[turn].put(link.link.dns, 1);
            threads_prop[turn].inc_dns();
            threads_prop[turn].setTo_crawl(queues[turn].size());
        }
    }

    private int get_turn()
    {
        if (attended_websites[0].size() < 10)
        {
            turn += 1;
            turn %= num_threads;
            return turn;
        }

        for (int i = 0; i < num_threads; i++)
        {
            if (queues[i].size() == 0)
            {
                turn = i;
                return turn;
            }
        }

        double mini = 1e10;
        int mini_ind = 0;
        for (int i = 0; i < num_threads; i++)
        {
            double q_sz = queues[i].size(), dns_sz = attended_websites[i].size();
            double score = (q_sz * dns_sz) / ((0.5 * q_sz) + (0.5 * dns_sz) + 0.000000001);
            if (score < mini)
            {
                mini = score;
                mini_ind = i;
            }
        }
        return mini_ind;
    }

    private double calc_priority(ObjLinkProp prop)
    {
        double k1 = 0.4, k2 = 0.4, k3 = 0.1, k4 = 0.1;
        double sum = prop.out_links + prop.sz_url + prop.sz_parent + prop.parent_priority;
        return (k1 * prop.out_links / sum) + (k2 * prop.sz_parent / sum) + (k3 * (1 - (prop.sz_url / sum))) + (k4 * prop.parent_priority / sum);
    }

    public void save_to_crawl()
    {
        System.out.println("Saving To crawl links waaaiiittt");
    }

    public void load_to_crawl()
    {
        System.out.println("Wait for loading To crawl links");
    }
}
