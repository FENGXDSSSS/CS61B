package bstmap;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<Key extends Comparable<Key>, Value> implements Map61B<Key, Value>{
    public BSTMap(){
        root = null;
        size = 0;
    }

    public BSTNode find(BSTNode root, Key k){
        if (root == null) {
            return root;
        }
        if (root.key.compareTo(k) == 0) {
            return root;
        }
        if (root.key.compareTo(k) > 0) {return find(root.left, k);}
        else {return find(root.right, k);}
    }

    @Override
    public void put(Key k, Value v) {
        if (containsKey(k)){
            BSTNode p = find(root, k);
            p.val = v;
        } else {
            root = putHelper(root, k, v);
            size += 1;
        }
    }

    public BSTNode putHelper(BSTNode root, Key k, Value v) {
        if (root == null){
            return new BSTNode(k, v);
        }
        if (root.key.compareTo(k) > 0) {root.left = putHelper(root.left, k, v);}
        else {root.right = putHelper(root.right, k, v);}

        return root;
    }

    @Override
    public Value get(Key k) {
        if (containsKey(k)){
            BSTNode tempnode = find(root, k);
            return tempnode.val;
        } else {
            return null;
        }
    }

    @Override
    public boolean containsKey(Key k) {
        return find(root, k) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        if (root == null) return;
        else {
            root.left = null;
            root.right = null;
            root = null;
        }
        size = 0;
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value remove(Key key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Value remove(Key k, Value v){
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }

    private class BSTNode {
        public BSTNode(Key k, Value v){
            left = null;
            right = null;
            key = k;
            val = v;
        }
        private BSTNode left;
        private BSTNode right;
        private Key key;
        private Value val;
    }

    private BSTNode root;
    private int size;
}
