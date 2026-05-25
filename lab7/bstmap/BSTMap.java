package bstmap;
import org.apache.commons.math3.ode.ODEIntegrator;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<Key extends Comparable<Key>, Value> implements Map61B<Key, Value>{
    public BSTMap(){
        root = null;
        size = 0;
    }

//    public BSTNode find(BSTNode root, Key k){
//        if (root == null) {
//            return root;
//        }
//        if (root.key.compareTo(k) == 0) {
//            return root;
//        }
//        if (root.key.compareTo(k) > 0) {return find(root.left, k);}
//        else {return find(root.right, k);}
//    }

    public Object[] find(BSTNode node, Key k) {
        if (node == null) { return null; }
        BSTNode parent = null;
        while (node != null) {
            int cmp = node.key.compareTo(k);

            if (cmp == 0) {
                if (node == root) {
                    return new Object[] {root, root, 0};
                } else if (parent.left == node) {
                    return new Object[] {parent, node, -1};
                } else {
                    return new Object[] {parent, node, 1};
                }
            }
            parent = node;
            if (cmp > 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return null;
    }

    @Override
    public void put(Key k, Value v) {
        BSTNode node = this.root;
        Object[] obj = find(node, k);
        if (obj == null){
            root = putHelper(root, k, v);
            size += 1;
        } else {
            node = (BSTNode) obj[1];
            node.val = v;
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
        Object[] p = find(root, k);
        if (p != null){
            BSTNode tempnode = (BSTNode) p[0];
            int direction = (int) p[2];
            if (direction == 0) {
                return tempnode.val;
            } else if (direction == -1) {
                return tempnode.left.val;
            } else {
                return tempnode.right.val;
            }
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
    public Value remove(Key k) {
        if (k == null) throw new IllegalArgumentException("calls delete() with a null key");
        Object[] obj = find(root, k);
        if (obj == null) { return null; }
        int direction = (int) obj[2];
        BSTNode node = (BSTNode) obj[0];
        BSTNode target = (BSTNode) obj[1];
        if (direction == 0) {
            Value value = node.val;
            if (isNoneChild(root)) { root = null; }
            else if (isOneChild(root)) {
                if (root.left != null) { root = root.left; }
                else { root = root.right; }
            } else {
                BSTNode mid = root.right;
                root = max(root);
                root.right = mid;
            }
            return value;
        }
        // 三种case
        Value value = (direction == -1 ? node.left : node.right).val;
        if (isNoneChild(target)) {
            if (direction == -1) { node.left = null; return value; }
            else { node.right = null; return value; }
        } else if (isOneChild(target)) {
            if ((direction == -1 ? node.left : node.right).left != null) {
                node.left = target.left;
            } else {
                node.right = target.right;
            }
        } else {
            if (direction == -1) {
                BSTNode mid = node.left.right;
                node.left = max(target);
                node.left.right = mid;
            } else {
                BSTNode mid = node.left.right;
                node.right = max(target);
                node.left.right = mid;
            }
        }
        return value;
    }

    @Override
    public Value remove(Key k, Value v){
        return null;
    }

    private boolean isNoneChild(BSTNode parent) {
        return parent.left == null && parent.right == null;
    }

    private boolean isOneChild(BSTNode parent) {
        if (parent.left == null && parent.right == null) {
            return false;
        } else if (parent.left != null && parent.right != null) {
            return false;
        } else if (parent.left != null && parent.right == null) {
            return true;
        } else {
            return true;
        }
    }

    private BSTNode max(BSTNode parent) {
        BSTNode target = null;
        BSTNode p = parent;
        while (p.left != null) {
            target = p;
            p = p.left;
        }

        return target;
    }

    @Override
    public Iterator iterator() {
        return new KeyIterator();
    }

    private class KeyIterator implements Iterator<Key>{

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Key next() {
            return null;
        }
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
