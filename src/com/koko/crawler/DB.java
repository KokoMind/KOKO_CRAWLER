package com.koko.crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import org.apache.commons.codec.digest.DigestUtils;

public class DB {

    private Connection connection_crawled = null;
    private Statement statement_crawled = null;
    private Connection connection_hasher = null;
    private Statement statement_hasher = null;
    private Connection connection_tocrawl = null;
    private Statement statement_tocrawl = null;

    public DB() {
        try {
            // create a database connection
            connection_hasher = DriverManager.getConnection("jdbc:sqlite:db/hasher.db");
            statement_hasher = connection_hasher.createStatement();
            statement_hasher.setQueryTimeout(30);
            connection_crawled = DriverManager.getConnection("jdbc:sqlite:db/crawled.db");
            statement_crawled = connection_crawled.createStatement();
            statement_crawled.setQueryTimeout(30);
            connection_tocrawl = DriverManager.getConnection("jdbc:sqlite:db/tocrawl.db");
            statement_tocrawl = connection_tocrawl.createStatement();
            statement_tocrawl.setQueryTimeout(30);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void finalize() {
        try {
            if (connection_hasher != null)
                connection_hasher.close();
            if (connection_crawled != null)
                connection_crawled.close();
            if (connection_tocrawl != null)
                connection_tocrawl.close();
        } catch (SQLException e) {
            // connection close failed.
            System.err.println(e);
        }
    }

    public int hash(String url) {
        try {
            statement_hasher.executeUpdate("insert into hasher values('" + DigestUtils.sha1Hex(url) + "');");
            return 0;
        } catch (SQLException e) {
            return -1;
        }
    }

    public int cache_url(String url, String dns, String content, int thread_id) {
        try {
            String put = String.valueOf(thread_id) + ",'" + url + "','" + dns + "','" + content + "','" + LocalDateTime.now() + "','" + LocalDateTime.now() + "','false'";
            statement_crawled.executeUpdate("insert into crawled (thread_id,url,dns,content,visited,last_visit,indexed) values(" + put + ");");
            return 0;
        } catch (SQLException e) {
            return -1;
        }
    }

    //TODO cache_to_crawl,get_to_crawl,delete_to_crawl
    public int cache_to_crawl() {
        return 0;
    }

    public int get_to_crawl() {
        return 0;
    }

    public int delete_to_crawl() {
        return 0;
    }

}
