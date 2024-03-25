package org.example.distanceapplication.cache;

import lombok.Getter;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LRUCache<K,V> implements Cache<K,V>{
    @Getter
    private HashMap<K,V> hashMap;
    @Getter
    private static final int MAXSIZE = 3;
    public LRUCache(){
        hashMap = new LinkedHashMap<>(10, 0.75f, true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<K,V> eldest){
                return size() > MAXSIZE;
            }
        };
    }

    @Override
    public Optional<V> get(K key) {
        Optional<V> result = Optional.empty();
        if(containsKey(key)) {
            result = Optional.of(hashMap.get(key));
        }
        return result;
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public void put(K key, V value) {
        hashMap.put(key, value);
        System.out.println(hashMap + " "  +hashMap.size() + "\n");

    }

    @Override
    public boolean containsKey(K key) {
        return hashMap.containsKey(key);
    }

    @Override
    public void remove(K key) {
        if (containsKey(key))
            hashMap.remove(key);
    }
}
