package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        public Node(Node l, T x, Node r) {
            prev = l;
            item = x;
            next = r;
        }

        public T item;
        public Node next;
        public Node prev;
    }
    public  LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public void addFirst(T x) {
        Node p = new Node(sentinel, x, sentinel.next);
        sentinel.next.prev = p;
        sentinel.next = p;

        size++;
    }

    @Override
    public void addLast(T x) {
        Node p = new Node(sentinel.prev, x, sentinel);
        sentinel.prev.next = p;
        sentinel.prev = p;

        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        while (p.next == sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node temp = sentinel.next;

        sentinel.next = temp.next;
        temp.next.prev = sentinel;

        size--;
        return temp.item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node temp = sentinel.prev;

        sentinel.prev = temp.prev;
        temp.prev.next = sentinel;

        size--;
        return temp.item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || size == 0 || index >= size) {
            return null;
        }
        Node temp = sentinel;
        while (index >= 0) {
            temp = temp.next;
            index--;
        }
        return temp.item;
    }

    @Override
    public Iterator<T> iterator() {
        return new LLDIterator();
    }

    private class LLDIterator implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return p != sentinel;
        }

        @Override
        public T next() {
            T temp = p.item;
            p = p.next;
            return temp;
        }

        private Node p = sentinel.next;
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        LinkedListDeque<?> other = ((LinkedListDeque<?>) o);

        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }

        return true;
    }

    private T helpRecursive(int index, Node p) {
        if (index < 0) {
            return p.item;
        }
        return helpRecursive(index - 1, p.next);
    }
    public T getRecursive(int index) {
        if (index < 0 || size == 0 || index >= size) {
            return null;
        }
        return helpRecursive(index, sentinel);
    }

    private Node sentinel;
    private int size;
}
