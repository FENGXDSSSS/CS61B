package deque;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void testAddFirst(){
        ArrayDeque list = new ArrayDeque();
        for(int i =0;i<90;i++){

            list.addFirst(i);
        }
        assertEquals(90,list.size());
    }
    @Test
    public void testRemoveFirst(){
        ArrayDeque list = new ArrayDeque();
        for(int i =0;i<90;i++){

            list.addFirst(i);
        }
        for(int i =0;i<90;i++){

            list.removeFirst();
        }
        assertEquals(0,list.size());
    }

    @Test
    public void testPrintDeque(){
        ArrayDeque list = new ArrayDeque();
        for(int i =0;i<90;i++){

            list.addFirst(i);
        }
        System.out.println("start test PrintDeque ,should be a list of 0-89");
        list.printDeque();
    }
    @Test
    public void testAddAndRemove(){
        ArrayDeque list = new ArrayDeque();
        for(int i =1;i<10;i++){
// 8 6 4 2 1 3 5 7 9
            if(i%2==0){
                list.addFirst(i);
                continue;
            }
            list.addLast(i);
        }


        int[] expectedArr= {8,6,4,2,1,3,5,7,9};
        for(int i=0;i<list.size();i++){
            assertEquals(expectedArr[i],list.get(i));
        }

    }
    @Test
    public void testGet(){
        ArrayDeque list = new ArrayDeque();
        int[] expectedArr = {9,8,7,6,5,4,3,2,1,0,999};
        for(int i =0;i<10;i++){
            list.addFirst(i);
//

        }
        list.addLast(999);
        for(int i =0;i<11;i++){
            if(i==10){
                assertEquals(expectedArr[i], list.get(i));
                assertNotEquals(0,list.get(i));
            }
            assertEquals(expectedArr[i],list.get(i));
        }

    }
    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests("all", ArrayDeque.class);
    }
}

//  return  items[(i+first)%items.length];

