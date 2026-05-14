package deque;

public class ArrayDeque<T> implements Deque<T> {
    public ArrayDeque(){
        list = (T[]) new Object[8];
        size = 0;
        nextfirst = list.length / 2;
        nextback = list.length / 2 + 1;
        arrlength = list.length;
    }
    // 数组扩容_居中复制
    public void resize(int capacity){
        T[] temp = (T[]) new Object[capacity];
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

    @Override
    public void addFirst(T x){
        if (size == arrlength){
            resize(size * 2);
        }
        if (nextfirst < 0){
            nextfirst = arrlength - 1;
        }
        list[nextfirst--] = x;

        size++;
    }

    @Override
    public void addLast(T x){
        if (size == arrlength){
            resize(size * 2);
        }
        if (nextback > arrlength - 1){
            nextback = 0;
        }
        list[nextback++] = x;

        size++;
    }

    @Override
    public int size(){return size;}

    @Override
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

    @Override
    public T removeFirst(){
        if (size == 0){
            return null;
        }
        if (nextfirst == arrlength - 1){
            nextfirst = 0;
        }else{
            nextfirst += 1;
        }
        T temp = list[nextfirst];
        list[nextfirst] = null;
        size--;
        return temp;
    }

    @Override
    public T removeLast(){
        if (size == 0){
            return null;
        }
        if (nextback == 0){
            nextback = arrlength - 1;
        }else{
            nextback -= 1;
        }
        T temp = list[nextback];
        list[nextback] = null;
        size--;
        return temp;
    }

    @Override
    public T get(int index){
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

    protected T[] list;
    protected int size;
    protected int nextfirst;
    protected int nextback;
    protected int arrlength;

}
