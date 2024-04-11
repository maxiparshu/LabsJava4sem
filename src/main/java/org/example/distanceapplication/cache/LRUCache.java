package org.example.distanceapplication.cache;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;


public class LRUCache<K, V> implements Cache<K, V> {
  @Getter
  private HashMap<K, V> hashMap;
  @Getter
  private static final int MAXSIZE = 10;

  public LRUCache() {
    hashMap = new LinkedHashMap<>(MAXSIZE * 2, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return size() > MAXSIZE;
      }
    };
  }

  @Override
  public Optional<V> get(final K key) {
    Optional<V> result = Optional.empty();
    if (containsKey(key)) {
      result = Optional.of(hashMap.get(key));
    }
    return result;
  }

  @Override
  public int size() {
    return hashMap.size();
  }

  @Override
  public void put(final K key, final V value) {
    hashMap.put(key, value);
  }

  @Override
  public boolean containsKey(final K key) {
    return hashMap.containsKey(key);
  }

  @Override
  public void remove(final K key) {
    if (containsKey(key)) {
      hashMap.remove(key);
    }
  }
}
