package deque;

public class ArrayDeque<Item> {
    public ArrayDeque(){
        list = (Item[]) new Object[8];
        size = 0;
        nextfirst = list.length / 2;
        nextback = list.length / 2 + 1;
        arrlength = list.length;
    }
    // 数组扩容_居中复制
    public void resize(int capacity){
        Item[] temp = (Item[]) new Object[capacity];
        int mid_mid = capacity / 4;
        int s_mid_mid = mid_mid;
        if (nextfirst + 1 < nextback - 1){
            System.arraycopy(list, 0, temp, mid_mid, arrlength);
        }else{
            for (int i = nextfirst + 1; i < arrlength; i++){
                temp[mid_mid++] = list[i];
            }
            for (int i = 0; i < nextfirst + 1; i++){
                temp[mid_mid++] = list[i];
            }
        }
        nextfirst = s_mid_mid - 1;
        nextback = s_mid_mid + arrlength;
        arrlength = capacity;
        list = temp;
    }

    public void addFirst(Item x){
        if (size == arrlength){
            resize(size * 2);
        }
        if (nextfirst < 0){
            nextfirst = arrlength - 1;
        }
        list[nextfirst--] = x;

        size++;
    }

    public void addLast(Item x){
        if (size == arrlength){
            resize(size * 2);
        }
        if (nextback > arrlength - 1){ // 因为索引值从零开始，所以最后一个元素为数组长度 - 1;
            nextback = 0;
        }
        list[nextback++] = x;

        size++;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){return size;}

    public void printDeque(){
        if (size == arrlength){
            for (int i = nextfirst + 1; i < arrlength; i++){
                System.out.print(list[i] + " ");
            }
            for (int i = 0; i < nextfirst + 1; i++){
                System.out.print(list[i] + " ");
            }
            System.out.println();
        }else{

        }
    }

    public Item removeFirst(){
        if (size == 0){
            return null;
        }
        if (nextfirst == arrlength - 1){
            nextfirst = 0;
        }else{
            nextfirst += 1;
        }
        Item temp = list[nextfirst];
        list[nextfirst] = null;
        size--;
        return temp;
    }

    public Item removelast(){
        if (size == 0){
            return null;
        }
        if (nextback == 0){
            nextback = arrlength - 1;
        }else{
            nextback -= 1;
        }
        Item temp = list[nextback];
        list[nextback] = null;
        size--;
        return temp;
    }

    public Item get(int index){
        if (index >= size || index < 0){
            return null;
        }
        return list[(nextfirst + 1 + index) % arrlength];
    }

//    public Iterator<Item> iterator(){
//
//    }

//    public boolean equals(Object o){
//
//    }

    private Item[] list;
    private int size;
    private int nextfirst;
    private int nextback;
    private int arrlength;

}
