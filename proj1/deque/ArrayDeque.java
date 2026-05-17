package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    public ArrayDeque() {
        list = (T[]) new Object[8];
        size = 0;
        nextfirst = list.length / 2;
        nextback = list.length / 2 + 1;
        arrlength = list.length;
    }
    // 数组扩容_居中复制
    private void resize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        int midMid = capacity / 4;
        int sMidMid = midMid;
        if (nextfirst + 1 < nextback - 1) {
            System.arraycopy(list, 0, temp, midMid, arrlength);
        } else {
            for (int i = nextfirst + 1; i < arrlength; i++) {
                temp[midMid++] = list[i];
            }
            for (int i = 0; i < nextfirst + 1; i++) {
                temp[midMid++] = list[i];
            }
        }
        nextfirst = sMidMid - 1;
        nextback = sMidMid + arrlength;
        arrlength = capacity;
        list = temp;
    }

    @Override
    public void addFirst(T x) {
        if (size == arrlength) {
            resize(size * 2);
        }
        if (nextfirst < 0) {
            nextfirst = arrlength - 1;
        }
        list[nextfirst--] = x;

        size++;
    }

    @Override
    public void addLast(T x) {
        if (size == arrlength) {
            resize(size * 2);
        }
        if (nextback > arrlength - 1) {
            nextback = 0;
        }
        list[nextback++] = x;

        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (size == arrlength) {
            for (int i = nextfirst + 1; i < arrlength; i++) {
                System.out.print(list[i] + " ");
            }
            for (int i = 0; i < nextfirst + 1; i++) {
                System.out.print(list[i] + " ");
            }
            System.out.println();
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (nextfirst == arrlength - 1) {
            nextfirst = 0;
        } else {
            nextfirst += 1;
        }
        T temp = list[nextfirst];
        list[nextfirst] = null;
        size--;
        if (size != 0 && arrlength / size >= 2 && arrlength > 8) {
            resizeSmall(arrlength / 2);
        }
        return temp;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (nextback == 0) {
            nextback = arrlength - 1;
        } else {
            nextback -= 1;
        }
        T temp = list[nextback];
        list[nextback] = null;
        size--;
        if (size != 0 && arrlength / size >= 2 && arrlength > 8) {
            resizeSmall(arrlength / 2);
        }
        return temp;
    }

    private void resizeSmall(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            temp[i] = this.get(i);
        }
        list = temp;
        nextfirst = -1;
        nextback = size;
        arrlength = capacity;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return list[(nextfirst + 1 + index) % arrlength];
    }

    @Override
    public Iterator<T> iterator() {
        return new ADIterator();
    }

    private class ADIterator implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return pos != size;
        }

        @Override
        public T next() {
            T temp = get(pos);
            pos++;
            return temp;
        }

        private int pos;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> other = (Deque<?>) o;
        if (this.size() != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!(this.get(i).equals(other.get(i)))) {
                return false;
            }
        }
        return true;

    }

    private T[] list;
    private int size;
    private int nextfirst;
    private int nextback;
    private int arrlength;

}
