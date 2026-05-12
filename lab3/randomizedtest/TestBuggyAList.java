package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> correct = new AListNoResizing<Integer>();
        BuggyAList<Integer> braken = new BuggyAList<Integer>();

        correct.addLast(4);
        correct.addLast(5);
        correct.addLast(6);

        braken.addLast(4);
        braken.addLast(5);
        braken.addLast(6);

        assertEquals(correct.size(), braken.size());

        assertEquals(correct.removeLast(), braken.removeLast());
        assertEquals(correct.removeLast(), braken.removeLast());
        assertEquals(correct.removeLast(), braken.removeLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            }
        }
    }
}
