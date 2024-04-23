package org.example.distanceapplication.cache;

import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LRUCacheTest {
  private LRUCache<String, String> cache;

  @BeforeEach
  void setUp() {
    cache = new LRUCache<>();
  }

  @Test
  public void getAndPutCheck() {
    String key = "123";
    var value = Optional.of("value");
    cache.put(key, value.get());

    var realValue = cache.get(key);

    assertEquals(value, realValue);
  }

  @Test
  public void removeCheck() {
    String key = "123";
    Optional<String> expectedValue = Optional.of("value");
    cache.put(key, expectedValue.get());

    cache.remove(key);

    assertEquals(cache.get(key), Optional.empty());
  }

  @Test
  public void containsCheck() {
    String key = "123";
    Optional<String> expectedValue = Optional.of("value");
    cache.put(key, expectedValue.get());

    assertTrue(cache.containsKey(key));
  }

  @Test
  public void checkSetterAndSize() {
    assertEquals(cache.size(), 0);
    String key = "123";
    String value = "value";
    cache.put(key, value);
    assertEquals(1, cache.size());
    var testHashMap = new HashMap<String, String>();
    testHashMap.put(key, value);
    assertEquals(testHashMap, cache.getHashMap());
    assertEquals(10, LRUCache.getMAXSIZE());
  }
}
