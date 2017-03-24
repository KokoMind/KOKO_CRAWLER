import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class WorkerSaver extends Thread implements IShutdownThreadParent
{

    private Thread t;
    private int id;
    private String name;
    private Frontier frontier;
    private Dashboard dash;
    private int crawled = 0;
    private volatile boolean keepOn = true;
    private ShutdownThread fShutdownThread;

    //Database Mongodb
    private int port_no;
    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> crawled_col;
    private List<Document> docs;

    //Measure time
    private long start;


    public WorkerSaver(int thread_id, String thread_name, Frontier front, Dashboard dash_, int port_no)
    {
        id = thread_id;
        name = thread_name;
        frontier = front;
        dash = dash_;
        this.setDaemon(true);
        //For interrupt
        fShutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(fShutdownThread);

        //Initializing Mongodb
        this.port_no = port_no;
        mongoClient = new MongoClient(new ServerAddress("localhost", port_no));
        db = mongoClient.getDatabase("crawled");
        crawled_col = db.getCollection("crawled");

        docs = new ArrayList<Document>();

        for (String name : db.listCollectionNames())
        {
            System.out.println(name);
        }
        System.out.println("a7a");
    }

    @Override
    public void shutdown()
    {
        keepOn = false;
    }

    public void run()
    {
        try
        {
            start = System.currentTimeMillis();
            while (keepOn)
            {
                ObjPage obj = frontier.pop_to_save();
                docs.add(obj.record);

                if (docs.size() >= 10)
                {
                    try
                    {
                        crawled_col.insertMany(docs);
                        double dur = (System.currentTimeMillis() - start)/1000.0/60.0;
                        System.out.println("Time Taken For 1000 page : " + String.valueOf(dur) + " Minutes");
                        start = System.currentTimeMillis();
                    }
                    catch (MongoException e)
                    {
                        e.printStackTrace();
                    }
                    docs = new ArrayList<Document>();
                }
            }
        }
        finally
        {
            crawled_col.insertMany(docs);
            mongoClient.close();
            System.out.println("Thread Saver id:-1 " + id + " exiting.");
        }
    }

    public void start()
    {
        System.out.println("Starting Thread Saver : " + id);
        if (t == null)
        {
            t = new Thread(this, name);
            t.start();
        }
    }
}

