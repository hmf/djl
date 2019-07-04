/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package software.amazon.ai.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code PairList} class provides an efficient way to access a list of key-value pair.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class PairList<K, V> implements Iterable<Pair<K, V>> {

    private List<K> keys;
    private List<V> values;

    /** Constructs an empty {@code PairList}. */
    public PairList() {
        keys = new ArrayList<>();
        values = new ArrayList<>();
    }

    /**
     * Constructs an empty {@code PairList} with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is negative
     */
    public PairList(int initialCapacity) {
        keys = new ArrayList<>(initialCapacity);
        values = new ArrayList<>(initialCapacity);
    }

    /**
     * Constructs a {@code PairList} containing the elements of the specified keys and values.
     *
     * @param keys the key list whose elements are to be placed into this PairList
     * @param values the value list whose elements are to be placed into this PairList
     * @throws IllegalArgumentException if the keys and values size are different
     */
    public PairList(List<K> keys, List<V> values) {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException("key value size mismatch.");
        }
        this.keys = keys;
        this.values = values;
    }

    /**
     * Constructs a {@code PairList} containing the elements of the specified map.
     *
     * @param map the map contains keys and values
     */
    public PairList(Map<K, V> map) {
        keys = new ArrayList<>(map.size());
        values = new ArrayList<>(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }
    }

    /**
     * Adds key and value to the list.
     *
     * @param key the key
     * @param value the value
     */
    public void add(K key, V value) {
        keys.add(key);
        values.add(value);
    }

    /**
     * Adds key-value pair to the list.
     *
     * @param pair key-value pair
     */
    public void add(Pair<K, V> pair) {
        keys.add(pair.getKey());
        values.add(pair.getValue());
    }

    /**
     * Returns the size of the list.
     *
     * @return the size of the list
     */
    public int size() {
        return keys.size();
    }

    /**
     * Returns the key-value pair at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the key-value pair at the specified position in this list
     */
    public Pair<K, V> get(int index) {
        return new Pair<>(keys.get(index), values.get(index));
    }

    /**
     * Returns the key at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the key at the specified position in this list
     */
    public K keyAt(int index) {
        return keys.get(index);
    }

    /**
     * Returns the value at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the value at the specified position in this list
     */
    public V valueAt(int index) {
        return values.get(index);
    }

    /**
     * Returns all keys of the list.
     *
     * @return all keys of the list
     */
    public List<K> keys() {
        return keys;
    }

    /**
     * Returns all values of the list.
     *
     * @return all values of the list
     */
    public List<V> values() {
        return values;
    }

    /**
     * Returns an array containing all of the keys in this list in proper sequence (from first to
     * last element); the runtime type of the returned array is that of the specified array.
     *
     * <p>If the list fits in the specified array, it is returned therein. Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of this list.
     *
     * @param target the array into which the keys of this list are to be stored, if it is big
     *     enough; otherwise, a new array of the same runtime type is allocated for this purpose.
     * @return an array containing the keys of this list
     */
    public K[] keyArray(K[] target) {
        return keys.toArray(target);
    }

    /**
     * Returns an array containing all of the values in this list in proper sequence (from first to
     * last element); the runtime type of the returned array is that of the specified array.
     *
     * <p>If the list fits in the specified array, it is returned therein. Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of this list.
     *
     * @param target the array into which the values of this list are to be stored, if it is big
     *     enough; otherwise, a new array of the same runtime type is allocated for this purpose.
     * @return an array containing the values of this list
     */
    public V[] valueArray(V[] target) {
        return values.toArray(target);
    }

    /**
     * Removes the key-value pair for the first key found in the list.
     *
     * @param key the key of the element to be removed
     * @return the value of the removed element, {@code null} if not found
     */
    public V remove(K key) {
        int index = keys.indexOf(key);
        if (index == -1) {
            return null;
        }
        keys.remove(index);
        return values.remove(index);
    }

    /**
     * Returns @{code true} if this list contains the specified key.
     *
     * @param key key whose presence in this list is to be tested
     * @return @{code true} if this list contains the specified key
     */
    public boolean contains(K key) {
        return keys.contains(key);
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Pair<K, V>> iterator() {
        return new Itr();
    }

    /**
     * Returns a Map that contains key value mapping of this list.
     *
     * @return a Map that contains key value mapping of this list
     */
    public Map<K, V> toMap() {
        return toMap(true);
    }

    /**
     * Returns a Map that contains key value mapping of this list.
     *
     * @param checkDuplicate {@code true} to make sure not duplicated keys in the list
     * @return a Map that contains key value mapping of this list
     */
    public Map<K, V> toMap(boolean checkDuplicate) {
        int size = keys.size();
        Map<K, V> map = new ConcurrentHashMap<>(size * 3 / 2);
        for (int i = 0; i < size; ++i) {
            if (map.put(keys.get(i), values.get(i)) != null && checkDuplicate) {
                throw new IllegalStateException("Duplicate keys: " + keys.get(i));
            }
        }
        return map;
    }

    /** Internal Iterator implementation. */
    private class Itr implements Iterator<Pair<K, V>> {

        private int cursor;
        private int size = size();

        Itr() {}

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public Pair<K, V> next() {
            if (cursor >= size) {
                throw new NoSuchElementException();
            }

            return get(cursor++);
        }
    }
}
