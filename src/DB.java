import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

public class DB
{

    private Connection connection_crawled = null;
    private Statement statement_crawled = null;
    private Connection connection_hasher = null;
    private Statement statement_hasher = null;

    public DB()
    {
        try
        {
            // create a database connection
            connection_hasher = DriverManager.getConnection("jdbc:sqlite::memory:");
            statement_hasher = connection_hasher.createStatement();
            statement_hasher.setQueryTimeout(30);
            connection_crawled = DriverManager.getConnection("jdbc:sqlite:./crawled.db");
            statement_crawled = connection_crawled.createStatement();
            statement_crawled.setQueryTimeout(30);

            String sql_create_table = "CREATE TABLE hasher (hash VARCHAR (42) NOT NULL PRIMARY KEY UNIQUE );";
            statement_hasher.executeUpdate(sql_create_table);

        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void finalize()
    {
        try
        {
            if (connection_hasher != null)
                connection_hasher.close();
            if (connection_crawled != null)
                connection_crawled.close();
        }
        catch (SQLException e)
        {
            // connection close failed.
            System.err.println(e);
        }
    }

    public int hash(String url)
    {
        try
        {
            statement_hasher.executeUpdate("insert into hasher values('" + DigestUtils.sha1Hex(url) + "');");
            return 0;
        }
        catch (SQLException e)
        {
            return -1;
        }
    }

    public int cache_url(String url, String dns, String content, int thread_id)
    {
        try
        {
            String query = "INSERT INTO crawled (thread_id,url,dns,content,visited,last_visit,indexed) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection_crawled.prepareStatement(query);
            ps.setInt(1, thread_id);
            ps.setString(2, url);
            ps.setString(3, dns);
            ps.setString(4, content);
            ps.setString(5, LocalDateTime.now().toString());
            ps.setString(6, LocalDateTime.now().toString());
            ps.setInt(7, 0);
            ps.addBatch();
            ps.executeBatch();
            return 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public int cache_to_crawl(ObjPQueue[][] arr)
    {
        Connection connection_tocrawl = null;
        try
        {
            connection_tocrawl = DriverManager.getConnection("jdbc:sqlite:tocrawl-" + LocalDateTime.now() + ".db");
//            connection_tocrawl = DriverManager.getConnection("jdbc:sqlite:db/tocrawl.db");
            Statement statement_tocrawl = connection_tocrawl.createStatement();
            statement_tocrawl.setQueryTimeout(30);

            String sql_create_table = "CREATE TABLE tocrawl ( id INTEGER NOT NULL PRIMARY KEY, url TEXT NOT NULL, dns VARCHAR (255) NOT NULL, value REAL NOT NULL);";
            statement_tocrawl.executeUpdate(sql_create_table);

            String query = "INSERT INTO tocrawl (url, dns, value) VALUES (?, ?, ?)";
            PreparedStatement ps = connection_tocrawl.prepareStatement(query);
            for (ObjPQueue[] array : arr)
            {
                for (ObjPQueue link : array)
                {
                    ps.setString(1, link.url);
                    ps.setString(2, link.dns);
                    ps.setDouble(3, link.value);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            connection_tocrawl.close();
            return 0;
        }
        catch (SQLException e)
        {
            if (connection_tocrawl != null)
                try
                {
                    connection_tocrawl.close();
                }
                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }
            e.printStackTrace();
            return -1;
        }
    }

    public ArrayList<ObjPQueue> get_to_crawl(String name, Integer num)
    {
        try
        {
            Connection connection_tocrawl = DriverManager.getConnection("jdbc:sqlite:db/" + name);
            Statement statement_tocrawl = connection_tocrawl.createStatement();
            statement_tocrawl.setQueryTimeout(30);
            System.out.println("Loading TOCRAWL links");
            ResultSet rs = null;
            ArrayList<ObjPQueue> ret = new ArrayList<ObjPQueue>();
            if (num == null)
                rs = statement_tocrawl.executeQuery("SELECT * FROM tocrawl;");
            else
                rs = statement_tocrawl.executeQuery("SELECT * FROM tocrawl where id < " + String.valueOf(num));
            while (rs.next())
            {
                ret.add(new ObjPQueue(rs.getString("url"), rs.getString("dns"), rs.getDouble("value")));
            }
            return ret;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
