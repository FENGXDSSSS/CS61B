package deque;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void testEmptySize() {
        ArrayDeque L = new ArrayDeque<Integer>();
        assertEquals(0, L.size());

    }

    @Test
    public void testAddFirst() {
        ArrayDeque L = new ArrayDeque<Integer>();
        for (int i = 10; i > 0; i--) {
            L.addFirst(i);
        }
        L.addFirst(0);
        assertEquals(11, L.size());
    }

    @Test
    public void testIsEmpty() {
        ArrayDeque L = new ArrayDeque<Integer>();
        assertTrue(L.isEmpty());
        L.addFirst(1);
        assertFalse(L.isEmpty());
    }

    @Test
    public void testPrintDeque() {
        ArrayDeque L = new ArrayDeque<Integer>();
        System.out.println("testing PrintDeque,\nexpected should be:\n1 2 3 4 5 6 7 8 9 10");
        for (int i = 10; i > 0; i--) {
            L.addFirst(i);

        }
        System.out.println("actual is :");
        L.printDeque();
    }

    @Test
    public void testRemoveFirst() {
        ArrayDeque L = new ArrayDeque<Integer>();

        for (int i = 10; i > 0; i--) {
            L.addFirst(i);

        }
        System.out.println("testing RemoveFirst,\nexpected should be:\n1 2 3 4 " +
                "5 6 7 8 9 10,the first item will disappear in turn");
        for (int i = 0; i < 10; i++) {

            L.removeFirst();
            L.printDeque();
            System.out.println();
        }

    }
    @Test
    public void testRemoveLast() {
        ArrayDeque L = new ArrayDeque<Integer>();

        for (int i = 10; i > 0; i--) {
            L.addFirst(i);

        }
        System.out.println("testing RemoveFirst,\noriginal list is:\n1 2 3 4 " +
                "5 6 7 8 9 10,the last item will disappear in turn");
        for (int i = 0; i < 10; i++) {

            L.removeLast();
            L.printDeque();
            System.out.println();
        }

    }
    @Test
    public void testGet() {
        ArrayDeque L = new ArrayDeque<Integer>();
        L.addFirst(4);
        L.addFirst(5);
        L.addFirst(6);
        L.addLast(5);
        L.addLast(6);
        L.addLast(8);
        L.removeLast();
        L.removeFirst();
        L.removeFirst();
        L.addFirst(3);
        L.addFirst(2);
        L.addFirst(1);
        L.addLast(9);
        L.removeFirst();

        System.out.println("test Get ,should be:\n 2 3 4 5 6 9\nactual is:\n");
        L.printDeque();
        assertEquals(L.get(5), 9);
        System.out.println("get the last item ,should be 9\nactual is:" + L.get(5));


    }

    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests("all", ArrayDequeTest.class);
    }

}
