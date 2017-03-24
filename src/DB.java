import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

public class DB
{

    public DB()
    {
        try
        {
            // create a database connection
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void finalize()
    {
        try
        {

        }
        catch (Exception e)
        {
            // connection close failed.
            System.err.println(e);
        }
    }

    public int cache()
    {
        try
        {
            return 0;
        }
        catch (Exception e)
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
