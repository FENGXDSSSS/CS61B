package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    @Override
    public void clear() {
        for (Collection<Node> t : buckets) {
            t.clear();
        }
        size = 0;
    }

    private int hashKey(K key) {
        return Math.floorMod(key.hashCode(),  buckets.length);
    }

    @Override
    public boolean containsKey(K key) {
        int hashKey = hashKey(key);
        if (buckets[hashKey] == null) { return false; }
        for (Node v : buckets[hashKey]) {
            if (v.key.equals(key)) { return true; }
        }
        return false;
    }

    private Node findNode(int hashkey, K key) {
        for (Node v : buckets[hashkey]) {
            if (v.key.equals(key)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        int hashKey = hashKey(key);
        if (containsKey(key)) {
            Node target = findNode(hashKey, key);
            return target.value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int hashKey = hashKey(key);
        if (isOverLoad()) {
            buckets = resizeBuckets(bucketsSize * 2);
        }
        if (buckets[hashKey] == null) {
            buckets[hashKey] = createBucket();
        }
        if (!containsKey(key)) {
            buckets[hashKey].add(new Node(key, value));
            size += 1;
        } else {
            findNode(hashKey, key).value = value;
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Collection<Node> t : buckets) {
            for (Node v : t) {
                set.add(v.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int hashKey = hashKey(key);
        if (containsKey(key)) {
            V val = findNode(hashKey, key).value;
            buckets[hashKey].remove(findNode(hashKey, key));
            return val;
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(8);
        bucketsSize = 8;
    }

    public MyHashMap(int initialSize) {
        size = 0;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        buckets = createTable(initialSize);
        this.loadFactor = maxLoad;
    }

        private double load() {
        return size / (double) buckets.length;
    }

    private boolean isOverLoad() {
        return load() >= loadFactor;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<Node>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = (Collection<Node>[]) new Collection[tableSize];
        return table;
    }

    private Collection<Node>[] resizeBuckets(int resize) {
        Collection<Node>[] newtable = (Collection<Node>[]) new Collection[resize];
        for (Collection<Node> t : buckets) {
            if (t == null) { continue; }
            for (Node v : t) {
                int hashKey = v.hashCode() % resize;
                if (newtable[hashKey] == null) {
                    newtable[hashKey] = createBucket();
                }
                newtable[hashKey].add(v);
            }
        }

        return newtable;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    private double loadFactor = 1.5;
    private int size;
    private int bucketsSize;
}
