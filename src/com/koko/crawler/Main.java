package com.koko.crawler;

import com.koko.crawler.DB;

public class Main {

    public static void main(String[] args) {
	// write your code here
        DB mydb = new DB();
        int ret = mydb.cache_url("www.google.com","192.168.1.1", "5lty", 1);
        if (ret == 0){
            System.out.print("Success");
        }else {
            System.out.print("Fail");
        }
    }
}
