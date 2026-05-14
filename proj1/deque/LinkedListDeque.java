package deque;

public class LinkedListDeque<Item> implements Deque<Item>{
    private class node{
        public node(node l, Item x, node r){
            prev = l;
            item = x;
            next = r;
        }

        public Item item;
        public node next;
        public node prev;
    }
    // 空链表构造
    public  LinkedListDeque(){
        sentinel = new node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }
    // 赋值链表构造
    public  LinkedListDeque(Item x){
        // 定义哨兵节点
        sentinel = new node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        // 定义新节点以连接哨兵节点
        node p = new node(sentinel.next, x, sentinel);
        sentinel.next = p;
        sentinel.prev = p;
        size = 1;
    }

    @Override
    public void addFirst(Item x){
        node p = new node(sentinel, x, sentinel.next);
        sentinel.next.prev = p;
        sentinel.next = p;

        size++;
    }

    @Override
    public void addLast(Item x){
        node p = new node(sentinel.prev, x, sentinel);
        sentinel.prev.next = p;
        sentinel.prev = p;

        size++;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque(){
        node p = sentinel.next;
        while (p.next == sentinel){
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public Item removeFirst(){
        if (size == 0){
            return null;
        }
        node temp = sentinel.next;

        sentinel.next = temp.next;
        temp.next.prev = sentinel;

        size--;
        return temp.item;
    }

    @Override
    public Item removeLast(){
        if (size == 0){
            return null;
        }
        node temp = sentinel.prev;

        sentinel.prev = temp.prev;
        temp.prev.next = sentinel;

        size--;
        return temp.item;
    }

    @Override
    public Item get(int index){
        if (index < 0 || size == 0 || index >= size){
            return null;
        }
        node temp = sentinel;
        while (index >= 0){
            temp = temp.next;
            index--;
        }
        return temp.item;
    }

//    public Iterator<Item> iterator(){
//
//    }

    public boolean equals(Object o){
        if (!(o instanceof LinkedListDeque)){
            return false;
        }
        LinkedListDeque<?> other = ((LinkedListDeque<?>) o);

        if (this.size() != other.size()){
            return false;
        }
        for (int i = 0; i < size; i++){
            if (!this.get(i).equals(other.get(i))){
                return false;
            }
        }

        return true;
    }
    // 辅助getRecursive递归的函数。
    public Item helpRecursive(int index, node p){
        if (index < 0){
            return p.item;
        }
        //index作为参数不用自减，不改变初值
        return helpRecursive(index - 1, p.next);
    }
    // 递归get
    public Item getRecursive(int index){
        if (index < 0 || size == 0 || index >= size){
            return null;
        }
        return helpRecursive(index, sentinel);
    }

    private node sentinel;
    private int size;
}
