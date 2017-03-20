package com.koko.crawler;


public class Controller
{

    public int num_workers;
    public Worker workers[];
    public Frontier frontier;
    public DB db;
    public Dashboard dash;

    public Controller(int num_threads, String[] seeds, boolean cont)
    {
        num_workers = num_threads;
        for (int i = 0; i < num_workers; i++)
        {
            num_workers = new Worker()
        }
    }

}
