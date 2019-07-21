package com.cat.cache;

import com.cat.cache.service.impl.CacheServiceImpl;
import com.cat.cache.service.impl.L2CacheServiceImpl;

import java.sql.SQLException;

public class CacheApplication {

    public static void main(String[] args) {
        try {
            L2CacheServiceImpl.run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CacheServiceImpl.run();
    }

}
