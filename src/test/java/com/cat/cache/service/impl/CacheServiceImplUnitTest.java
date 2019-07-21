package com.cat.cache.service.impl;

import com.cat.cache.domain.CacheObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CacheServiceImplUnitTest {

    @Mock
    CacheServiceImpl cacheService = new CacheServiceImpl();

    @Mock
    L2CacheServiceImpl l2CacheService;

    @Before
    public void setup() {
        cacheService.l2CacheServiceImpl = l2CacheService;
    }

    @After
    public void cleanup() {
        cacheService.cacheMap.clear();
    }

    @Test
    public void whenCacheExpired_CacheMapShouldBeEmpty() throws InterruptedException {
        CacheObject expiredObj1 = new CacheObject("A", System.currentTimeMillis()-200);
        CacheObject expiredObj2 = new CacheObject("B", System.currentTimeMillis()-100);
        cacheService.cacheMap.put("a",expiredObj1);
        cacheService.cacheMap.put("b",expiredObj2);
        assertTrue(cacheService.cacheMap.size() == 2);

        //When run() is called, all expired objects are removed and the map should be empty.
        cacheService.run();
        Thread.sleep(5000);
        assertEquals(0, cacheService.cacheMap.size());

    }

    @Test
    public void whenKeyIsNull_DoNothing() {
        doNothing().when(cacheService).put(isA(String.class), isA(Object.class));
        cacheService.put(null, null);
        verify(cacheService, times(1)).put(null, null);
    }

    @Test
    public void whenPuttingKeyValueInMap_withKeyValid_AndValueNull_RemoveKeyFromMap() {
        CacheObject obj = new CacheObject("A", System.currentTimeMillis());
        cacheService.cacheMap.put("a", obj);

        assertTrue(cacheService.cacheMap.size() == 1);

        doCallRealMethod().when(cacheService).put(any(), any());
        cacheService.put("a", null);

        assertTrue(cacheService.cacheMap.size() == 0);

    }

    @Test
    public void whenPuttingKeyValueInMap_withKeyAndValueValid_PutInL1CacheMap() {
        doNothing().when(cacheService.l2CacheServiceImpl).put(isA(String.class), isA(CacheObject.class));
        doCallRealMethod().when(cacheService).put(any(), any());
        cacheService.put("a", "RandomValue");

        assertEquals(1, cacheService.cacheMap.size());
        assertSame("RandomValue", cacheService.cacheMap.get("a").getValue());
        verify(cacheService, times(1)).put("a", "RandomValue");
        verify(cacheService.l2CacheServiceImpl, times(1)).put(any(String.class), any(CacheObject.class));

    }

    @Test(expected = IllegalArgumentException.class)
    public void whenGetIsCalled_withNullKey_IllegalArgumentExceptionIsThrown() {
        doCallRealMethod().when(cacheService).get(any());
        cacheService.get(null);
    }

    //@Test
    public void whenGetIsCalled_withValidKey_ValueIsReturned() throws InterruptedException {
        doCallRealMethod().when(cacheService).get(any());
        cacheService.cacheMap.put("alpha", new CacheObject("RandomValue", System.currentTimeMillis()+12000));
        Thread.sleep(5000);
        Object value = cacheService.get("alpha");
        verify(cacheService, times(1)).get("alpha");
        assertEquals("RandomValue", value);
    }

    @Test
    public void whenGetIsCalled_withValidKeyAndValueIsNotPresentInL1Cache_L2CacheIsCalled() {
        doCallRealMethod().when(cacheService).get(any());
        Object value = cacheService.get("a");
        assertEquals(null, value);
        verify(cacheService, times(1)).get("a");
        verify(cacheService.l2CacheServiceImpl, times(1)).get("a");
    }

}
