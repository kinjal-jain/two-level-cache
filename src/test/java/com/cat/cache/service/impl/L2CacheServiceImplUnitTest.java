package com.cat.cache.service.impl;

import com.cat.cache.domain.CacheObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;

@RunWith(MockitoJUnitRunner.class)
public class L2CacheServiceImplUnitTest {

    @Mock
    CacheServiceImpl cacheService = new CacheServiceImpl();

    @Mock
    L2CacheServiceImpl l2CacheService = new L2CacheServiceImpl();

    @Before
    public void setup() {
        cacheService.l2CacheServiceImpl = l2CacheService;
        try {
            l2CacheService.run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanup() {
        cacheService.l2CacheServiceImpl = l2CacheService;
        try {
            l2CacheService.run();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whenValuePutInL1Cache_AlsoPutInL2Cache() {
        doCallRealMethod().when(cacheService).put(any(), any());
        doCallRealMethod().when(l2CacheService).put(any(), any());
        doCallRealMethod().when(l2CacheService).get(any());
        cacheService.put("a", "abc");
        Object value = l2CacheService.get("a");
        assertEquals("abc", value);
    }

    @Test
    public void whenValueForKeyNotPresent_returnErrorMessage() {
        doCallRealMethod().when(cacheService).put(any(), any());
        doCallRealMethod().when(l2CacheService).put(any(), any());
        doCallRealMethod().when(l2CacheService).get(any());
        Object value = l2CacheService.get("a");
        assertEquals("Value not found for Key : a", value);
    }

    @Test
    public void whenValueNotInL1Cache_ItIsRetrievedFromL2Cache() {
        doCallRealMethod().when(cacheService).put(any(), any());
        doCallRealMethod().when(l2CacheService).put(any(), any());
        doCallRealMethod().when(cacheService).get(any());
        doCallRealMethod().when(l2CacheService).get(any());
        cacheService.put("alpha", "String one");
        cacheService.put("beta", "String two");

        //Removing key:value from L1 cache to test scenario of bringing value from L2 cache
        cacheService.cacheMap.remove("b");
        Object value1PresentInL2Cache = l2CacheService.get("alpha");
        Object value2PresentInL2Cache = l2CacheService.get("beta");

        //testing that the value is fetched from L2 as its not present in L1.
        Object result = cacheService.get("beta");
        assertEquals("String one", value1PresentInL2Cache);
        assertEquals("String two", value2PresentInL2Cache);
        assertEquals("String two", result);
    }

    @Test
    public void whenValueNotInL1Cache_ItIsRetrievedFromL2Cache_AndSetInL1Cache() {
        doCallRealMethod().when(cacheService).put(any(), any());
        doCallRealMethod().when(l2CacheService).put(any(), any());
        doCallRealMethod().when(cacheService).get(any());
        doCallRealMethod().when(l2CacheService).get(any());
        cacheService.put("alpha", "String one");
        cacheService.put("beta", "String two");

        //Removing key:value from L1 cache to test scenario of bringing value from L2 cache
        cacheService.cacheMap.remove("b");
        Object value1PresentInL2Cache = l2CacheService.get("alpha");
        Object value2PresentInL2Cache = l2CacheService.get("beta");

        //testing that the value is fetched from L2 as its not present in L1.
        Object result = cacheService.get("beta");

        //As the value is now restored in L1 Cache after retrieval from L1 Cache.
        CacheObject cacheObjectFromL1Cache = (CacheObject) cacheService.cacheMap.get("beta");
        Object valueFromL1Cache = cacheObjectFromL1Cache.getValue();
        assertEquals("String two", valueFromL1Cache);

        assertEquals("String one", value1PresentInL2Cache);
        assertEquals("String two", value2PresentInL2Cache);
        assertEquals("String two", result);
    }
}
